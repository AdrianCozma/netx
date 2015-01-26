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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.rules.RuleChain.outerRule;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;
import org.kaazing.netx.URLConnectionHelper;
import org.kaazing.netx.ws.WsURLConnection;

/**
 * RFC-6455, section 5.2 "Base Framing Protocol"
 */
public class BaseFramingIT {

    private final Random random = new Random();

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/ws/framing");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "echo.binary.payload.length.0/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadLength0() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        OutputStream out = connection.getOutputStream();
        InputStream in = connection.getInputStream();

        byte[] writeBytes = new byte[0];
        random.nextBytes(writeBytes);
        out.write(writeBytes);

        byte[] readBytes = new byte[0];
        in.read(readBytes);

        k3po.join();

        assertArrayEquals(writeBytes, readBytes);

        connection.close();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.125/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadLength125() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        OutputStream out = connection.getOutputStream();
        InputStream in = connection.getInputStream();

        byte[] writeBytes = new byte[125];
        random.nextBytes(writeBytes);
        out.write(writeBytes);

        byte[] readBytes = new byte[125];
        in.read(readBytes);

        k3po.join();

        assertArrayEquals(writeBytes, readBytes);

        connection.close();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.126/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadLength126() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        OutputStream out = connection.getOutputStream();
        InputStream in = connection.getInputStream();

        byte[] writeBytes = new byte[126];
        random.nextBytes(writeBytes);
        out.write(writeBytes);

        byte[] readBytes = new byte[126];
        in.read(readBytes);

        k3po.join();

        assertArrayEquals(writeBytes, readBytes);

        connection.close();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.127/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadLength127() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        OutputStream out = connection.getOutputStream();
        InputStream in = connection.getInputStream();

        byte[] writeBytes = new byte[127];
        random.nextBytes(writeBytes);
        out.write(writeBytes);

        byte[] readBytes = new byte[127];
        in.read(readBytes);

        k3po.join();

        assertArrayEquals(writeBytes, readBytes);

        connection.close();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.128/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadLength128() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        OutputStream out = connection.getOutputStream();
        InputStream in = connection.getInputStream();

        byte[] writeBytes = new byte[128];
        random.nextBytes(writeBytes);
        out.write(writeBytes);

        byte[] readBytes = new byte[128];
        in.read(readBytes);

        k3po.join();

        assertArrayEquals(writeBytes, readBytes);

        connection.close();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.65535/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadLength65535() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        OutputStream out = connection.getOutputStream();
        InputStream in = connection.getInputStream();

        byte[] writeBytes = new byte[65535];
        random.nextBytes(writeBytes);
        out.write(writeBytes);

        byte[] readBytes = new byte[65535];
        in.read(readBytes);

        k3po.join();

        assertArrayEquals(writeBytes, readBytes);

        connection.close();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.65536/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadLength65536() throws Exception {
        URLConnectionHelper helper = URLConnectionHelper.newInstance();
        URI location = URI.create("ws://localhost:8080/path");

        WsURLConnection connection = (WsURLConnection) helper.openConnection(location);
        OutputStream out = connection.getOutputStream();
        InputStream in = connection.getInputStream();

        byte[] writeBytes = new byte[65536];
        random.nextBytes(writeBytes);
        out.write(writeBytes);

        byte[] readBytes = new byte[65536];
        in.read(readBytes);

        k3po.join();

        assertArrayEquals(writeBytes, readBytes);

        connection.close();
    }

    @Test
    @Specification({
        "echo.text.payload.length.0/handshake.request.and.frame",
        "echo.text.payload.length.0/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadLength0() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.125/handshake.request.and.frame",
        "echo.text.payload.length.125/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadLength125() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.126/handshake.request.and.frame",
        "echo.text.payload.length.126/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadLength126() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.127/handshake.request.and.frame",
        "echo.text.payload.length.127/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadLength127() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.128/handshake.request.and.frame",
        "echo.text.payload.length.128/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadLength128() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.65535/handshake.request.and.frame",
        "echo.text.payload.length.65535/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadLength65535() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.65536/handshake.request.and.frame",
        "echo.text.payload.length.65536/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadLength65536() throws Exception {
        k3po.join();
    }

}
