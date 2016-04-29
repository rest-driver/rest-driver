/**
 * Copyright © 2010-2011 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.restdriver.clientdriver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.clientdriver.exception.ClientDriverInternalException;
import com.github.restdriver.clientdriver.exception.ClientDriverSetupException;
import com.github.restdriver.clientdriver.jetty.ClientDriverJettyHandler;

/**
 * The main class which acts as a facade for the Client Driver.
 */
public class ClientDriver {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDriver.class);
    protected Server jettyServer;
    private ServerConnector jettyServerConnector;
    private int port = -1;
    private List<ClientDriverListener> listeners = new ArrayList<ClientDriverListener>();
    protected ClientDriverJettyHandler handler;
    
    /**
     * Constructor. This will find a free port, bind to it and start the server
     * up before it returns.
     * 
     * @param handler
     *            The {@link ClientDriverJettyHandler} to use.
     */
    public ClientDriver(ClientDriverJettyHandler handler) {
        this(handler, 0);
    }
    
    /**
     * Constructor. This will bind to the given port and start the server
     * up before it returns.
     * 
     * @param handler
     *            The {@link ClientDriverJettyHandler} to use.
     * @param port
     *            The port to listen on. Expect startup errors if this port is
     *            not free.
     */
    public ClientDriver(ClientDriverJettyHandler handler, int port) {
        this.handler = handler;
        this.jettyServer = createAndStartJetty(port);
    }

    /**
     * Convenience constructor for extending classes. This allows overwriting
     * and customization of the setup procedure.
     */
    protected ClientDriver() {

    }

    protected Server createAndStartJetty(int port) {
        Server jetty = new Server();
        jetty.setHandler(handler);
        ServerConnector connector = createConnector(jetty, port);
        jetty.addConnector(connector);
        try {
            jetty.start();
        } catch (Exception e) {
            throw new ClientDriverSetupException("Error starting jetty on port " + port, e);
        }
        this.port = connector.getLocalPort();
        this.jettyServerConnector = connector;
        return jetty;
    }

    protected SslContextFactory getSslContextFactory() {
        return null;
    }

    protected ServerConnector createConnector(Server jetty, int port) {
        ServerConnector connector = new ServerConnector(jetty, getSslContextFactory());
        connector.setHost(null);
        connector.setPort(port);
        return connector;
    }

    protected void replaceConnector(ServerConnector newConnector, Server jetty) {
        // get current connector and shut him down
        jettyServerConnector.shutdown();
        try {
            jettyServerConnector.stop();
        } catch (Exception e) {
            throw new ClientDriverInternalException("Error shutting down jetty connector during replacement", e);
        }
        jetty.removeConnector(jettyServerConnector);
        // add new and start him up
        jetty.addConnector(newConnector);
        try {
            newConnector.start();
        } catch (Exception e) {
            throw new ClientDriverInternalException("Error starting new jetty connector during replacement", e);
        }
    }
    
    public int getPort() {
        return port;
    }
    
    /**
     * Get the base URL which the ClientDriver is running on.
     * 
     * @return <p>The base URL, which will be like "http://localhost:xxxx".</p>
     *         <p><b>There is no trailing slash on this</b></p>
     */
    public String getBaseUrl() {
        return "http://localhost:" + port;
    }
    
    /**
     * Verifies that all expectations have been met and nothing unexpected has been requested.
     * 
     * If the verification fails, a {@link ClientDriverFailedExpectationException} is thrown with plenty of detail, and your test will fail!
     */
    public void verify() {
        LOGGER.info("Beginning verification");
        handler.checkForUnexpectedRequests();
        handler.checkForUnmatchedExpectations();
    }
    
    /**
     * Make the mock not fail fast on an unexpected request.
     */
    public void noFailFastOnUnexpectedRequest() {
        handler.noFailFastOnUnexpectedRequest();
    }
    
    /**
     * Resets the expectations and requests in the handler.
     */
    public void reset() {
        handler.reset();
    }
    
    /**
     * Shutdown the server without verifying expectations.
     */
    public void shutdownQuietly() {
        try {
            jettyServer.stop();
        } catch (Exception e) {
            throw new ClientDriverInternalException("Error shutting down jetty", e);
        } finally {
            completed();
        }
    }
    
    /**
     * Shutdown the server and calls {@link #verify()}.
     */
    public void shutdown() {
        shutdownQuietly();
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
     * @return The newly added expectation.
     */
    public ClientDriverExpectation addExpectation(ClientDriverRequest request, ClientDriverResponse response) {
        return handler.addExpectation(request, response);
    }
    
    void addListener(ClientDriverListener listener) {
        listeners.add(listener);
    }
    
    private void completed() {
        for (ClientDriverListener listener : listeners) {
            listener.hasCompleted();
        }
    }

    public void verify(ClientDriverRequest clientDriverRequest, int times) {
        handler.verify(clientDriverRequest, times);
    }
}
