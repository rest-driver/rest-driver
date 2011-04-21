package com.github.restdriver.serverdriver.http;

import com.github.restdriver.clientdriver.example.ClientDriverUnitTest;
import com.github.restdriver.serverdriver.http.exception.RuntimeHttpHostConnectException;
import com.github.restdriver.serverdriver.http.exception.RuntimeUnknownHostException;
import com.github.restdriver.serverdriver.http.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static com.github.restdriver.serverdriver.RestServerDriver.*;
import static com.github.restdriver.serverdriver.Matchers.hasStatusCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 13:52
 */
public class HttpProblemsAcceptanceTest extends ClientDriverUnitTest {

    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = super.getClientDriver().getBaseUrl();
    }

    @Test(expected = RuntimeUnknownHostException.class)
    public void getWithNoSuchHostThrowsException() {
        Response response = get("http://no-such-host");
    }

    @Test(expected = IllegalStateException.class)
    public void getWithInvalidProtocolThrowsException() {
        Response response = get("xyzzz://no-such-host");
    }

    @Test(expected = RuntimeHttpHostConnectException.class)
    public void noServerListeningThrowsException() {
        Response response = get("http://localhost:" + getFreePort());
    }

    
    /**
     * @see "http://chaoticjava.com/posts/retrieving-a-free-port-for-socket-binding/"
     */
    private int getFreePort() {
        try{
            ServerSocket server = new ServerSocket(0);
            int port = server.getLocalPort();
            server.close();
            return port;

        } catch (IOException ioe){
            throw new RuntimeException(ioe);
        }
    }


}
