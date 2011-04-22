package com.github.restdriver.serverdriver.http;

import static com.github.restdriver.serverdriver.RestServerDriver.*;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;

import com.github.restdriver.serverdriver.http.exception.RuntimeHttpHostConnectException;
import com.github.restdriver.serverdriver.http.exception.RuntimeUnknownHostException;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 13:52
 */
public class HttpProblemsAcceptanceTest {

    @Test(expected = RuntimeUnknownHostException.class)
    public void getWithNoSuchHostThrowsException() {
        get("http://no-such-host");
    }

    @Test(expected = IllegalStateException.class)
    public void getWithInvalidProtocolThrowsException() {
        get("xyzzz://no-such-host");
    }

    @Test(expected = RuntimeHttpHostConnectException.class)
    public void noServerListeningThrowsException() {
        get("http://localhost:" + getFreePort());
    }

    /**
     * @see "http://chaoticjava.com/posts/retrieving-a-free-port-for-socket-binding/"
     */
    private int getFreePort() {
        try {
            ServerSocket server = new ServerSocket(0);
            int port = server.getLocalPort();
            server.close();
            return port;

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
