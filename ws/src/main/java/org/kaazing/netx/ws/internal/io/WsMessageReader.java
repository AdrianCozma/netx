/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.netx.ws.internal.io;

import static java.lang.Character.charCount;
import static java.lang.Character.toChars;
import static java.lang.String.format;
import static org.kaazing.netx.ws.WsURLConnection.WS_PROTOCOL_ERROR;
import static org.kaazing.netx.ws.internal.ext.flyweight.Opcode.BINARY;
import static org.kaazing.netx.ws.internal.ext.flyweight.Opcode.CLOSE;
import static org.kaazing.netx.ws.internal.ext.flyweight.Opcode.CONTINUATION;
import static org.kaazing.netx.ws.internal.ext.flyweight.Opcode.TEXT;
import static org.kaazing.netx.ws.internal.util.Utf8Util.initialDecodeUTF8;
import static org.kaazing.netx.ws.internal.util.Utf8Util.remainingBytesUTF8;
import static org.kaazing.netx.ws.internal.util.Utf8Util.remainingDecodeUTF8;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;

import org.kaazing.netx.ws.internal.DefaultWebSocketContext;
import org.kaazing.netx.ws.internal.WsURLConnectionImpl;
import org.kaazing.netx.ws.internal.ext.WebSocketContext;
import org.kaazing.netx.ws.internal.ext.flyweight.Flyweight;
import org.kaazing.netx.ws.internal.ext.flyweight.Frame;
import org.kaazing.netx.ws.internal.ext.flyweight.FrameRO;
import org.kaazing.netx.ws.internal.ext.flyweight.FrameRW;
import org.kaazing.netx.ws.internal.ext.flyweight.Opcode;
import org.kaazing.netx.ws.internal.ext.function.WebSocketFrameConsumer;
import org.kaazing.netx.ws.internal.util.OptimisticReentrantLock;

public final class WsMessageReader extends MessageReader {
    private static final String MSG_NULL_CONNECTION = "Null HttpURLConnection passed in";
    private static final String MSG_INDEX_OUT_OF_BOUNDS = "offset = %d; (offset + length) = %d; buffer length = %d";
    private static final String MSG_NON_BINARY_FRAME = "Non-text frame - opcode = 0x%02X";
    private static final String MSG_NON_TEXT_FRAME = "Non-binary frame - opcode = 0x%02X";
    private static final String MSG_BUFFER_SIZE_SMALL = "Buffer's remaining capacity %d too small for payload of size %d";
    private static final String MSG_RESERVED_BITS_SET = "Protocol Violation: Reserved bits set 0x%02X";
    private static final String MSG_UNRECOGNIZED_OPCODE = "Protocol Violation: Unrecognized opcode %d";
    private static final String MSG_FIRST_FRAME_FRAGMENTED = "Protocol Violation: First frame cannot be a fragmented frame";
    private static final String MSG_UNEXPECTED_OPCODE = "Protocol Violation: Opcode 0x%02X expected only in the initial frame";
    private static final String MSG_FRAGMENTED_CONTROL_FRAME = "Protocol Violation: Fragmented control frame 0x%02X";
    private static final String MSG_FRAGMENTED_FRAME = "Protocol Violation: Fragmented frame 0x%02X";
    private static final String MSG_MAX_MESSAGE_LENGTH = "Message length %d is greater than the maximum allowed %d";

    private final WsURLConnectionImpl connection;
    private final InputStream in;
    private final FrameRW incomingFrame;
    private final FrameRO incomingFrameRO;
    private final ByteBuffer heapBuffer;
    private final ByteBuffer heapBufferRO;
    private final byte[] networkBuffer;
    private final Lock stateLock;

    private int networkBufferReadOffset;
    private int networkBufferWriteOffset;
    private byte[] applicationByteBuffer;
    private char[] applicationCharBuffer;
    private int applicationBufferWriteOffset;
    private int applicationBufferLength;
    private int codePoint;
    private int remainingBytes;
    private MessageType type;
    private State state;
    private boolean fragmented;

    private enum State {
        INITIAL, PROCESS_MESSAGE_TYPE, PROCESS_FRAME;
    };

    final WebSocketFrameConsumer terminalBinaryFrameConsumer = new WebSocketFrameConsumer() {
        @Override
        public void accept(WebSocketContext context, Frame frame) throws IOException {
            Opcode opcode = frame.opcode();
            long xformedPayloadLength = frame.payloadLength();
            int xformedPayloadOffset = frame.payloadOffset();

            switch (opcode) {
            case BINARY:
            case CONTINUATION:
                if ((opcode == BINARY) && fragmented) {
                    byte leadByte = (byte) Flyweight.uint8Get(frame.buffer(), frame.offset());
                    connection.doFail(WS_PROTOCOL_ERROR, format(MSG_FRAGMENTED_FRAME, leadByte));
                }

                if ((opcode == CONTINUATION) && !fragmented) {
                    byte leadByte = (byte) Flyweight.uint8Get(frame.buffer(), frame.offset());
                    connection.doFail(WS_PROTOCOL_ERROR, format(MSG_FRAGMENTED_FRAME, leadByte));
                }

                if (applicationBufferWriteOffset + xformedPayloadLength > applicationByteBuffer.length) {
                    // MessageReader requires reading the entire message/frame. So, if there isn't enough space to read the
                    // frame, we should throw an exception.
                    int available = applicationByteBuffer.length - applicationBufferWriteOffset;
                    throw new IOException(format(MSG_BUFFER_SIZE_SMALL, available, xformedPayloadLength));
                }

                // Using System.arraycopy() to copy the contents of transformed.buffer().array() to the applicationBuffer
                // results in java.nio.ReadOnlyBufferException as we will be getting a RO flyweight in the terminal consumer.
                for (int i = 0; i < xformedPayloadLength; i++) {
                    applicationByteBuffer[applicationBufferWriteOffset++] = frame.buffer().get(xformedPayloadOffset + i);
                }
                fragmented = !frame.fin();
                break;
            default:
                connection.doFail(WS_PROTOCOL_ERROR, format(MSG_NON_BINARY_FRAME, Opcode.toInt(opcode)));
                break;
            }
        }
    };

    private final WebSocketFrameConsumer terminalTextFrameConsumer = new WebSocketFrameConsumer() {
        @Override
        public void accept(WebSocketContext context, Frame frame) throws IOException {
            Opcode opcode = frame.opcode();
            long xformedPayloadLength = frame.payloadLength();
            int xformedPayloadOffset = frame.payloadOffset();

            switch (opcode) {
            case TEXT:
            case CONTINUATION:
                if ((opcode == TEXT) && fragmented) {
                    byte leadByte = (byte) Flyweight.uint8Get(frame.buffer(), frame.offset());
                    connection.doFail(WS_PROTOCOL_ERROR, format(MSG_FRAGMENTED_FRAME, leadByte));
                }

                if ((opcode == CONTINUATION) && !fragmented) {
                    byte leadByte = (byte) Flyweight.uint8Get(frame.buffer(), frame.offset());
                    connection.doFail(WS_PROTOCOL_ERROR, format(MSG_FRAGMENTED_FRAME, leadByte));
                }

                int charsConverted = utf8BytesToChars(frame.buffer(),
                                                      xformedPayloadOffset,
                                                      xformedPayloadLength,
                                                      applicationCharBuffer,
                                                      applicationBufferWriteOffset,
                                                      applicationBufferLength);
                applicationBufferWriteOffset += charsConverted;
                applicationBufferLength -= charsConverted;
                fragmented = !frame.fin();
                break;
            default:
                connection.doFail(WS_PROTOCOL_ERROR, format(MSG_NON_BINARY_FRAME, Opcode.toInt(opcode)));
                break;
            }
        }
    };

    private final WebSocketFrameConsumer terminalControlFrameConsumer = new WebSocketFrameConsumer() {
        @Override
        public void accept(WebSocketContext context, Frame frame) throws IOException {
            Opcode opcode = frame.opcode();

            switch (opcode) {
            case CLOSE:
                connection.sendCloseIfNecessary(frame);
                break;
            case PING:
                connection.sendPong(frame);
                break;
            case PONG:
                break;
            default:
                connection.doFail(WS_PROTOCOL_ERROR, format(MSG_UNRECOGNIZED_OPCODE, Opcode.toInt(opcode)));
                break;
            }
        }
    };

    public WsMessageReader(WsURLConnectionImpl connection) throws IOException {
        if (connection == null) {
            throw new NullPointerException(MSG_NULL_CONNECTION);
        }

        int maxFrameLength = connection.getMaxFrameLength();

        this.connection = connection;
        this.in = connection.getTcpInputStream();
        this.state = State.INITIAL;
        this.incomingFrame = new FrameRW();
        this.incomingFrameRO = new FrameRO();
        this.stateLock = new OptimisticReentrantLock();

        this.fragmented = false;
        this.applicationBufferWriteOffset = 0;
        this.applicationBufferLength = 0;
        this.networkBufferReadOffset = 0;
        this.networkBufferWriteOffset = 0;
        this.networkBuffer = new byte[maxFrameLength];
        this.heapBuffer = ByteBuffer.wrap(networkBuffer);
        this.heapBufferRO = heapBuffer.asReadOnlyBuffer();
    }

    @Override
    public int read(byte[] buf) throws IOException {
        return this.read(buf, 0, buf.length);
    }

    @Override
    public int read(final byte[] buf, final int offset, final int length) throws IOException {
        if (buf == null) {
            throw new NullPointerException("Null buf passed in");
        }
        else if ((offset < 0) || ((offset + length) > buf.length) || (length < 0)) {
            int len = offset + length;
            throw new IndexOutOfBoundsException(format(MSG_INDEX_OUT_OF_BOUNDS, offset, len, buf.length));
        }

        try {
            stateLock.lock();

            // Check whether next() has been invoked before this method. If it wasn't invoked, then read the header byte.
            switch (state) {
            case INITIAL:
            case PROCESS_MESSAGE_TYPE:
                readMessageType();
                break;
            default:
                break;
            }

            applicationByteBuffer = buf;
            applicationBufferWriteOffset = offset;

            boolean finalFrame = false;

            do {
                switch (type) {
                case EOS:
                    return -1;
                case TEXT:
                    throw new IOException(MSG_NON_BINARY_FRAME);
                default:
                    break;
                }

                incomingFrame.wrap(heapBuffer, networkBufferReadOffset);
                finalFrame = incomingFrame.fin();

                validateOpcode();
                DefaultWebSocketContext context = connection.getIncomingContext();
                IncomingSentinelExtension sentinel = (IncomingSentinelExtension) context.getSentinelExtension();
                sentinel.setTerminalConsumer(terminalBinaryFrameConsumer, incomingFrame.opcode());
                connection.processIncomingFrame(incomingFrameRO.wrap(heapBufferRO, networkBufferReadOffset));
                networkBufferReadOffset += incomingFrame.length();
                state = State.PROCESS_MESSAGE_TYPE;

                if (networkBufferReadOffset == networkBufferWriteOffset) {
                    networkBufferReadOffset = 0;
                    networkBufferWriteOffset = 0;
                }

                if (!finalFrame) {
                    // Start reading the CONTINUATION frame for the message.
                    assert state == State.PROCESS_MESSAGE_TYPE;
                    readMessageType();
                }
            } while (!finalFrame);

            state = State.INITIAL;

            if ((applicationBufferWriteOffset - offset == 0) && (length > 0)) {
                // An extension can consume the entire message and not let it surface to the app. In which case, we just try to
                // read the next message.
                return read(buf, offset, length);
            }

            return applicationBufferWriteOffset - offset;
        }
        catch (IOException ex) {
            throw ex;
        }
        finally {
            stateLock.unlock();
        }
    }

    @Override
    public int read(char[] buf) throws IOException {
        return this.read(buf, 0, buf.length);
    }

    @Override
    public int read(final char[] buf, int offset, int length) throws IOException {
        if (buf == null) {
            throw new NullPointerException("Null buf passed in");
        }
        else if ((offset < 0) || ((offset + length) > buf.length) || (length < 0)) {
            int len = offset + length;
            throw new IndexOutOfBoundsException(format(MSG_INDEX_OUT_OF_BOUNDS, offset, len, buf.length));
        }

        try {
            stateLock.lock();

            // Check whether next() has been invoked before this method. If it wasn't invoked, then read the header byte.
            switch (state) {
            case INITIAL:
            case PROCESS_MESSAGE_TYPE:
                readMessageType();
                break;
            default:
                break;
            }

            applicationCharBuffer = buf;
            applicationBufferWriteOffset = offset;
            applicationBufferLength = length;

            boolean finalFrame = false;

            do {
                switch (type) {
                case EOS:
                    return -1;
                case BINARY:
                    throw new IOException(MSG_NON_TEXT_FRAME);
                default:
                    break;
                }

                incomingFrame.wrap(heapBuffer, networkBufferReadOffset);
                finalFrame = incomingFrame.fin();

                validateOpcode();
                DefaultWebSocketContext context = connection.getIncomingContext();
                IncomingSentinelExtension sentinel = (IncomingSentinelExtension) context.getSentinelExtension();
                sentinel.setTerminalConsumer(terminalTextFrameConsumer, incomingFrame.opcode());
                connection.processIncomingFrame(incomingFrameRO.wrap(heapBufferRO, networkBufferReadOffset));
                networkBufferReadOffset += incomingFrame.length();
                state = State.PROCESS_MESSAGE_TYPE;

                if (networkBufferReadOffset == networkBufferWriteOffset) {
                    networkBufferReadOffset = 0;
                    networkBufferWriteOffset = 0;
                }

                if (!finalFrame) {
                    // Start reading the CONTINUATION frame for the message.
                    assert state == State.PROCESS_MESSAGE_TYPE;
                    readMessageType();
                }
            } while (!finalFrame);

            state = State.INITIAL;

            if ((applicationBufferWriteOffset - offset == 0) && (length > 0)) {
                // An extension can consume the entire message and not let it surface to the app. In which case, we just try to
                // read the next message.
                return read(buf, offset, length);
            }

            return applicationBufferWriteOffset - offset;
        }
        catch (IOException ex) {
            throw ex;
        }
        finally {
            stateLock.lock();
        }
    }

    @Override
    public MessageType peek() {
        return type;
    }

    @Override
    public MessageType next() throws IOException {
        try {
            stateLock.lock();

            switch (state) {
            case INITIAL:
            case PROCESS_MESSAGE_TYPE:
                readMessageType();
                break;
            default:
                break;
            }

            return type;
        }
        catch (IOException ex) {
            throw ex;
        }
        finally {
            stateLock.unlock();
        }
    }

    public void close() throws IOException {
        try {
            stateLock.lock();
            in.close();
            type = null;
            state = null;
        }
        finally {
            stateLock.unlock();
        }
    }

    private int readMessageType() throws IOException {
        assert state == State.PROCESS_MESSAGE_TYPE || state == State.INITIAL;

        if (networkBufferWriteOffset == 0) {
            int bytesRead = in.read(networkBuffer, 0, networkBuffer.length);
            if (bytesRead == -1) {
                type = MessageType.EOS;
                return -1;
            }

            networkBufferReadOffset = 0;
            networkBufferWriteOffset = bytesRead;
        }

        int numBytes = ensureFrameMetadata();
        if (numBytes == -1) {
            type = MessageType.EOS;
            return -1;
        }

        incomingFrame.wrap(heapBuffer, networkBufferReadOffset);
        int payloadLength = incomingFrame.payloadLength();

        if (incomingFrame.offset() + payloadLength > networkBufferWriteOffset) {
            if (payloadLength > networkBuffer.length) {
                int maxPayloadLength = connection.getMaxMessageLength();
                throw new IOException(format(MSG_MAX_MESSAGE_LENGTH, payloadLength, maxPayloadLength));
            }
            else {
                // Enough space. But may need shifting the frame to the beginning to be able to fit the payload.
                if (incomingFrame.offset() + payloadLength > networkBuffer.length) {
                    int len = networkBufferWriteOffset - networkBufferReadOffset;
                    System.arraycopy(networkBuffer, networkBufferReadOffset, networkBuffer, 0, len);
                    networkBufferReadOffset = 0;
                    networkBufferWriteOffset = len;
                }
            }

            int frameLength = connection.getFrameLength(false, payloadLength);
            int remainingBytes = networkBufferReadOffset + frameLength - networkBufferWriteOffset;
            while (remainingBytes > 0) {
                int bytesRead = in.read(networkBuffer, networkBufferWriteOffset, remainingBytes);
                if (bytesRead == -1) {
                    type = MessageType.EOS;
                    return -1;
                }

                remainingBytes -= bytesRead;
                networkBufferWriteOffset += bytesRead;
            }

            incomingFrame.wrap(heapBuffer, networkBufferReadOffset);
        }

        int leadByte = Flyweight.uint8Get(incomingFrame.buffer(), incomingFrame.offset());
        int flags = incomingFrame.flags();

        switch (flags) {
        case 0:
            break;
        default:
            connection.doFail(WS_PROTOCOL_ERROR, format(MSG_RESERVED_BITS_SET, flags));
            break;
        }

        Opcode opcode = null;

        try {
            opcode = incomingFrame.opcode();
        }
        catch (Exception ex) {
            connection.doFail(WS_PROTOCOL_ERROR, format(MSG_UNRECOGNIZED_OPCODE, leadByte & 0x0F));
        }

        switch (opcode) {
        case CONTINUATION:
            if (state == State.INITIAL) {
                // The first frame cannot be a fragmented frame..
                type = MessageType.EOS;
                connection.doFail(WS_PROTOCOL_ERROR, MSG_FIRST_FRAME_FRAGMENTED);
            }
            break;
        case TEXT:
            if (state == State.PROCESS_MESSAGE_TYPE) {
                // In a subsequent fragmented frame, the opcode should NOT be set.
                connection.doFail(WS_PROTOCOL_ERROR, format(MSG_UNEXPECTED_OPCODE, Opcode.toInt(TEXT)));
            }
            type = MessageType.TEXT;
            break;
        case BINARY:
            if (state == State.PROCESS_MESSAGE_TYPE) {
                // In a subsequent fragmented frame, the opcode should NOT be set.
                connection.doFail(WS_PROTOCOL_ERROR, format(MSG_UNEXPECTED_OPCODE, Opcode.toInt(BINARY)));
            }
            type = MessageType.BINARY;
            break;
        case CLOSE:
        case PING:
        case PONG:
            if (!incomingFrame.fin()) {
                // Control frames cannot be fragmented.
                connection.doFail(WS_PROTOCOL_ERROR, MSG_FRAGMENTED_CONTROL_FRAME);
            }

            DefaultWebSocketContext context = connection.getIncomingContext();
            IncomingSentinelExtension sentinel = (IncomingSentinelExtension) context.getSentinelExtension();
            sentinel.setTerminalConsumer(terminalControlFrameConsumer, incomingFrame.opcode());
            connection.processIncomingFrame(incomingFrameRO.wrap(heapBufferRO, networkBufferReadOffset));
            networkBufferReadOffset += incomingFrame.length();

            if (networkBufferReadOffset == networkBufferWriteOffset) {
                networkBufferReadOffset = 0;
                networkBufferWriteOffset = 0;
            }

            if (opcode == CLOSE) {
                type = MessageType.EOS;
                return -1;
            }
            leadByte = readMessageType();
            break;
        default:
            connection.doFail(WS_PROTOCOL_ERROR, format(MSG_UNRECOGNIZED_OPCODE, opcode.ordinal()));
            break;
        }

        state = State.PROCESS_FRAME;
        return leadByte;
    }

    private int utf8BytesToChars(
            ByteBuffer src,
            int srcOffset,
            long srcLength,
            char[] dest,
            int destOffset,
            int destLength) throws IOException {
        int destMark = destOffset;
        int index = 0;

        while (index < srcLength) {
            int b = -1;

            while (codePoint != 0 || ((index < srcLength) && (remainingBytes > 0))) {
                // Surrogate pair.
                if (codePoint != 0 && remainingBytes == 0) {
                    int charCount = charCount(codePoint);
                    if (charCount > destLength) {
                        int len = destOffset + charCount;
                        throw new IndexOutOfBoundsException(format(MSG_INDEX_OUT_OF_BOUNDS, destOffset, len, destLength));
                    }
                    toChars(codePoint, dest, destOffset);
                    destOffset += charCount;
                    destLength -= charCount;
                    codePoint = 0;
                    break;
                }

                // EOP
                if (index == srcLength) {
                    // We have multi-byte chars split across WebSocket frames.
                    break;
                }

                b = src.get(srcOffset++);
                index++;

                // character encoded in multiple bytes
                codePoint = remainingDecodeUTF8(codePoint, remainingBytes--, b);
            }

            if (index < srcLength) {
                b = src.get(srcOffset++);
                index++;

                // Detect whether character is encoded using multiple bytes.
                remainingBytes = remainingBytesUTF8(b);
                switch (remainingBytes) {
                case 0:
                    // No surrogate pair.
                    int asciiCodePoint = initialDecodeUTF8(remainingBytes, b);
                    assert charCount(asciiCodePoint) == 1;
                    toChars(asciiCodePoint, dest, destOffset++);
                    destLength--;
                    break;
                default:
                    codePoint = initialDecodeUTF8(remainingBytes, b);
                    break;
                }
            }
        }

        return destOffset - destMark;
    }

    private int ensureFrameMetadata() throws IOException {
        int offsetDiff = networkBufferWriteOffset - networkBufferReadOffset;
        if (offsetDiff > 10) {
            // The payload length information is definitely available in the network buffer.
            return 0;
        }

        int bytesRead = 0;
        int maxMetadata = 10;
        int length = maxMetadata - offsetDiff;
        int frameMetadataLength = 2;

        // Ensure that the networkBuffer at the very least contains the payload length related bytes.
        switch (offsetDiff) {
        case 1:
            System.arraycopy(networkBuffer, networkBufferReadOffset, networkBuffer, 0, offsetDiff);
            networkBufferWriteOffset = offsetDiff;  // no break
        case 0:
            length = frameMetadataLength - offsetDiff;
            while (length > 0) {
                bytesRead = in.read(networkBuffer, offsetDiff, length);
                if (bytesRead == -1) {
                    return -1;
                }

                length -= bytesRead;
                networkBufferWriteOffset += bytesRead;
            }
            networkBufferReadOffset = 0;           // no break;
        default:
            // int b1 = networkBuffer[networkBufferReadOffset]; // fin, flags and opcode
            int b2 = networkBuffer[networkBufferReadOffset + 1] & 0x7F;

            if (b2 > 0) {
                switch (b2) {
                case 126:
                    frameMetadataLength += 2;
                    break;
                case 127:
                    frameMetadataLength += 8;
                    break;
                default:
                    break;
                }

                if (offsetDiff >= frameMetadataLength) {
                    return 0;
                }

                int remainingMetadata = networkBufferReadOffset + frameMetadataLength - networkBufferWriteOffset;
                if (networkBuffer.length <= networkBufferWriteOffset + remainingMetadata) {
                    // Shift the frame to the beginning of the buffer and try to read more bytes to be able to figure out
                    // the payload length.
                    System.arraycopy(networkBuffer, networkBufferReadOffset, networkBuffer, 0, offsetDiff);
                    networkBufferReadOffset = 0;
                    networkBufferWriteOffset = offsetDiff;
                }

                while (remainingMetadata > 0) {
                    bytesRead = in.read(networkBuffer, networkBufferWriteOffset, remainingMetadata);
                    if (bytesRead == -1) {
                        return -1;
                    }

                    remainingMetadata -= bytesRead;
                    networkBufferWriteOffset += bytesRead;
                }
            }
        }

        return bytesRead;
    }

    private void validateOpcode() throws IOException {
        int leadByte = Flyweight.uint8Get(incomingFrame.buffer(), incomingFrame.offset());
        try {
            incomingFrame.opcode();
        }
        catch (Exception ex) {
            connection.doFail(WS_PROTOCOL_ERROR, format(MSG_UNRECOGNIZED_OPCODE, leadByte & 0x0F));
        }
    }
}
