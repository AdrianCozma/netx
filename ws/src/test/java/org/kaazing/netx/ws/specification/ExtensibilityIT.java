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

package org.kaazing.netx.ws.specification;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertSame;
import static org.junit.rules.RuleChain.outerRule;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;
import org.kaazing.netx.URLConnectionHelper;
import org.kaazing.netx.ws.WsURLConnection;
import org.kaazing.netx.ws.internal.WsURLConnectionImpl;
import org.kaazing.netx.ws.internal.io.MessageReader;
import org.kaazing.netx.ws.internal.io.MessageType;

/**
 * RFC-6455, section 5.8 "Extensibility"
 */
public class ExtensibilityIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/ws/extensibility");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test(expected = IOException.class)
    @Specification({
        "server.send.text.frame.with.rsv.1/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendTextFrameWithRsv1() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.binary.frame.with.rsv.1/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendBinaryFrameWithRsv1() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.close.frame.with.rsv.1/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendCloseFrameWithRsv1() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.ping.frame.with.rsv.1/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPingFrameWithRsv1() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.pong.frame.with.rsv.1/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPongFrameWithRsv1() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.text.frame.with.rsv.2/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendTextFrameWithRsv2() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.binary.frame.with.rsv.2/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendBinaryFrameWithRsv2() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.close.frame.with.rsv.2/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendCloseFrameWithRsv2() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.ping.frame.with.rsv.2/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPingFrameWithRsv2() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.pong.frame.with.rsv.2/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPongFrameWithRsv2() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.text.frame.with.rsv.3/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendTextFrameWithRsv3() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        char[] cbuf = new char[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case TEXT:
                    reader.read(cbuf);
                    break;
                default:
                    assertSame(MessageType.TEXT, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.binary.frame.with.rsv.3/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendBinaryFrameWithRsv3() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        byte[] readBytes = new byte[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case BINARY:
                    reader.read(readBytes);
                    break;
                default:
                    assertSame(MessageType.BINARY, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.close.frame.with.rsv.3/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendCloseFrameWithRsv3() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        byte[] readBytes = new byte[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case BINARY:
                    reader.read(readBytes);
                    break;
                default:
                    assertSame(MessageType.BINARY, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.ping.frame.with.rsv.3/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPingFrameWithRsv3() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        byte[] readBytes = new byte[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case BINARY:
                    reader.read(readBytes);
                    break;
                default:
                    assertSame(MessageType.BINARY, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.pong.frame.with.rsv.3/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPongFrameWithRsv3() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        byte[] readBytes = new byte[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case BINARY:
                    reader.read(readBytes);
                    break;
                default:
                    assertSame(MessageType.BINARY, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.text.frame.with.rsv.4/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendTextFrameWithRsv4() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.binary.frame.with.rsv.4/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendBinaryFrameWithRsv4() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.close.frame.with.rsv.4/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendCloseFrameWithRsv4() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.ping.frame.with.rsv.4/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPingFrameWithRsv4() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.pong.frame.with.rsv.4/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPongFrameWithRsv4() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.text.frame.with.rsv.5/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendTextFrameWithRsv5() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.binary.frame.with.rsv.5/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendBinaryFrameWithRsv5() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.close.frame.with.rsv.5/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendCloseFrameWithRsv5() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.ping.frame.with.rsv.5/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPingFrameWithRsv5() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.pong.frame.with.rsv.5/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPongFrameWithRsv5() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.text.frame.with.rsv.6/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendTextFrameWithRsv6() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        char[] cbuf = new char[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case TEXT:
                    reader.read(cbuf);
                    break;
                default:
                    assertSame(MessageType.TEXT, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.binary.frame.with.rsv.6/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendBinaryFrameWithRsv6() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        byte[] readBytes = new byte[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case TEXT:
                    reader.read(readBytes);
                    break;
                default:
                    assertSame(MessageType.BINARY, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.close.frame.with.rsv.6/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendCloseFrameWithRsv6() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        byte[] readBytes = new byte[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case TEXT:
                    reader.read(readBytes);
                    break;
                default:
                    assertSame(MessageType.BINARY, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.ping.frame.with.rsv.6/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPingFrameWithRsv6() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        byte[] readBytes = new byte[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case TEXT:
                    reader.read(readBytes);
                    break;
                default:
                    assertSame(MessageType.BINARY, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.pong.frame.with.rsv.6/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPongFrameWithRsv6() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        MessageReader reader = ((WsURLConnectionImpl) connection).getMessageReader();
        byte[] readBytes = new byte[0];

        try {
            for (MessageType type = reader.next(); type != MessageType.EOS; type = reader.next()) {
                switch (type) {
                case TEXT:
                    reader.read(readBytes);
                    break;
                default:
                    assertSame(MessageType.BINARY, type);
                    break;
                }
            }
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.text.frame.with.rsv.7/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendTextFrameWithRsv7() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        Reader reader = connection.getReader();

        try {
            reader.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.binary.frame.with.rsv.7/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendBinaryFrameWithRsv7() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.close.frame.with.rsv.7/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendCloseFrameWithRsv7() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.ping.frame.with.rsv.7/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPingFrameWithRsv7() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

    @Test(expected = IOException.class)
    @Specification({
        "server.send.pong.frame.with.rsv.7/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenServerSendPongFrameWithRsv7() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        InputStream input = connection.getInputStream();

        try {
            input.read();
        }
        finally {
            k3po.finish();
        }
    }

}
