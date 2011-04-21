package com.github.restdriver.clientdriver;

import java.io.IOException;
import java.net.ServerSocket;

import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.clientdriver.exception.ClientDriverSetupException;
import com.github.restdriver.clientdriver.jetty.ClientDriverJettyHandler;
import org.eclipse.jetty.server.Server;

import com.github.restdriver.types.ClientDriverRequest;
import com.github.restdriver.types.ClientDriverResponse;

/**
 * The main class which acts as a facade for the Client Driver
 */
public class ClientDriver {

    private final Server jettyServer;
    private final int portNum;

    private final ClientDriverJettyHandler handler;

    /**
     * Constructor. This will find a free port, bind to it and start the server up before it returns.
     * 
     * @param handler
     *            The {@link ClientDriverJettyHandler} to use.
     */
    public ClientDriver(final ClientDriverJettyHandler handler) {

        this.handler = handler;

        try {
            portNum = getFreePort();
        } catch (final IOException ioe) {
            throw new ClientDriverSetupException("Error finding free port for webserver", ioe);
        }

        jettyServer = new Server(portNum);

        try {

            jettyServer.setHandler(handler.getJettyHandler());
            jettyServer.start();

        } catch (final Exception e) {

            throw new ClientDriverSetupException("Error starting jetty", e);

        }
    }

    /**
     * Get the base URL which the BenchServer is running on.
     * 
     * @return The base URL, which will be like "http://localhost:xxxxx". <br/>
     *         <b>There is no trailing slash on this</b>
     */
    public String getBaseUrl() {
        return "http://localhost:" + portNum;
    }

    /**
     * @see "http://chaoticjava.com/posts/retrieving-a-free-port-for-socket-binding/"
     */
    private int getFreePort() throws IOException {
        final ServerSocket server = new ServerSocket(0);
        final int port = server.getLocalPort();
        server.close();
        return port;
    }

    /**
     * Shutdown the server. This also verifies that all expectations have been met and nothing unexpected has been
     * requested. If the verification fails, a {@link com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException} is thrown with plenty of
     * detail, and your test will fail!
     */
    public void shutdown() {

        try {

            jettyServer.stop();

        } catch (final Exception e) {

            throw new ClientDriverFailedExpectationException("Error shutting down jetty", e);

        }

        verify();

    }

    /**
     * Add in an expected {@link ClientDriverRequest}/{@link ClientDriverResponse} pair.
     * 
     * @param request
     *            The expected request
     * @param response
     *            The response to serve to that request
     * 
     */
    public void addExpectation(final ClientDriverRequest request, final ClientDriverResponse response) {
        handler.addExpectation(request, response);
    }

    private void verify() {

        handler.checkForUnexpectedRequests();
        handler.checkForUnmatchedExpectations();

    }
}
