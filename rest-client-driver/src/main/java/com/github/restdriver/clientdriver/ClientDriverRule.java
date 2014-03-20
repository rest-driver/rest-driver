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

import java.util.concurrent.TimeUnit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ClientDriverRule allows a user to specify expectations on the HTTP requests that are made against it.
 */
public final class ClientDriverRule implements TestRule {
    
    private final ClientDriver clientDriver;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDriverRequest.class);
    
    private static final long IMMEDIATELY = 0;
    private long expectedResponseTimeout = IMMEDIATELY;
    
    /**
     * Creates a new rule with a driver running on a free port.
     */
    public ClientDriverRule() {
        clientDriver = new ClientDriverFactory().createClientDriver();
    }
    
    /**
     * Creates a new rule with a driver running on the specified port.
     * 
     * @param port The port on which the driver should listen
     */
    public ClientDriverRule(int port) {
        clientDriver = new ClientDriverFactory().createClientDriver(port);
    }
    
    @Override
    public Statement apply(Statement base, Description description) {
        return new ClientDriverStatement(base);
    }
    
    /**
     * Adds an expectation on the ClientDriver to expect the given request and response.
     * 
     * @param request The request to expect
     * @param response The response to expect
     * 
     * @return The newly added expectation.
     */
    public ClientDriverExpectation addExpectation(ClientDriverRequest request, ClientDriverResponse response) {
        LOGGER.info("addExpectation: {} {}", request.getMethod(), request.getPath());
        
        if (!response.canExpire() && (expectedResponseTimeout > 0)) {
            response.within(expectedResponseTimeout, TimeUnit.MILLISECONDS);
        }
        
        return clientDriver.addExpectation(request, response);
    }
    
    /**
     * The base URL of the underlying ClientDriver.
     * 
     * @return The base URL String <b>There is no trailing slash on this</b>.
     */
    public String getBaseUrl() {
        return clientDriver.getBaseUrl();
    }

    /**
     * The port that the underlying ClientDriver is listening too.
     *
     * @return the listening port.
     */
    public int getPort() {
        return clientDriver.getPort();
    }

    /**
     * The given listener will be registered with the Client Driver and executes once execution has
     * completed.
     * 
     * @param listener The listener
     */
    public void whenCompleted(ClientDriverCompletedListener listener) {
        clientDriver.addListener(listener);
    }
    
    /**
     * <p>
     * When ClientDriver comes to check for unmatched expectations, it will retry for the specified length of time before considering the expectation unmatched.
     * </p>
     * <p>
     * Has the effect of calling {@link ClientDriverResponse#within(long, java.util.concurrent.TimeUnit)} against each {@link ClientDriverResponse} added as an expectation. (Note that a timeout set
     * explicitly against the {@link ClientDriverResponse} overrides the value set here.)
     * </p>
     * 
     * @param timeout
     *            The timeout expressed in the specified units.
     * @param units
     *            The {@link TimeUnit} that the timeout is expressed in.
     */
    public ClientDriverRule expectResponsesWithin(final int timeout, final TimeUnit units) {
        expectedResponseTimeout = units.toMillis(timeout);
        return this;
    }
    
    /**
     * Statement which evaluates the given Statement and shuts down the client after evaluation.
     */
    private class ClientDriverStatement extends Statement {
        
        private final Statement statement;
        
        public ClientDriverStatement(Statement statement) {
            this.statement = statement;
        }
        
        @Override
        public void evaluate() throws Throwable {
            
            try {
                AssertionError assertionError = null;
                try {
                    statement.evaluate();
                } catch (AssertionError e) {
                    assertionError = e;
                }
                
                clientDriver.verify();
                if (assertionError != null) {
                    throw assertionError;
                }
            } finally {
                clientDriver.shutdownQuietly();
            }
        }
        
    }
    
}
