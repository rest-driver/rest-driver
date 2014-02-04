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

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.clientdriver.exception.ClientDriverSetupException;
import com.github.restdriver.clientdriver.jetty.ClientDriverJettyHandler;

/**
 * The main class which acts as a facade for the Client Driver.
 */
public final class ClientDriver {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDriver.class);
    private static final int MAX_TRIES = 3;
    
    private final Server jettyServer;
    private int port;
    private final List<ClientDriverListener> listeners = new ArrayList<ClientDriverListener>();
    private final ClientDriverJettyHandler handler;
    
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
    
    private Server createAndStartJetty(int port) {
        int tries = triesForPort(port);
        
        for (int retries = 0; retries < tries; retries++) {
            Server jetty = new Server(port);
            jetty.setHandler(handler);
            
            for (Connector connector : jetty.getConnectors()) {
                if (connector instanceof AbstractNetworkConnector) {
                    ((AbstractNetworkConnector) connector).setHost("0.0.0.0");
                }
            }
            
            try {
                jetty.start();
                for (Connector connector : jetty.getConnectors()) {
                    if (connector instanceof NetworkConnector) {
                        this.port = ((NetworkConnector) connector).getLocalPort();
                        break;
                    }
                }
                return jetty;
            } catch (BindException e) {
                if (retries < tries - 1) {
                    LOGGER.warn("Could not bind to port; trying again");

                }
            } catch (Exception e) {
                throw new ClientDriverSetupException(
                        "Error starting jetty on port " + port, e);
                
            }
        }
        
        throw new ClientDriverSetupException(
                "Error starting jetty on port " + port + " after " + tries + " retries", null);
    }
    
    private int triesForPort(int port) {
        return port == 0 ? MAX_TRIES : 1;
    }
    
    public int getPort() {
        return port;
    }
    
    /**
     * Get the base URL which the ClientDriver is running on.
     * 
     * @return The base URL, which will be like "http://localhost:xxxxx". <br/>
     *         <b>There is no trailing slash on this</b>
     */
    public String getBaseUrl() {
        return "http://localhost:" + port;
    }
    
    /**
     * Gets a free port on localhost for binding to.
     * 
     * @see "http://chaoticjava.com/posts/retrieving-a-free-port-for-socket-binding/"
     * 
     * @return The port number.
     */
    public static int getFreePort() {
        
        try {
            ServerSocket server = new ServerSocket(0);
            int port = server.getLocalPort();
            server.close();
            return port;
            
        } catch (IOException ioe) {
            throw new ClientDriverSetupException(
                    "IOException finding free port", ioe);
        }
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
     * Shutdown the server without verifying expectations.
     */
    public void shutdownQuietly() {
        try {
            jettyServer.stop();
        } catch (Exception e) {
            throw new ClientDriverFailedExpectationException("Error shutting down jetty", e);
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
    
    public void reset() {
        handler.reset();
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
}
