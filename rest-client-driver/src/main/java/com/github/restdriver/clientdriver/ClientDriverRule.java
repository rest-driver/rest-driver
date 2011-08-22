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

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * The ClientDriverRule allows a user to specify expectations on the HTTP requests that are made against it.
 */
public final class ClientDriverRule implements MethodRule {

    private final ClientDriver clientDriver;
    
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
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new ClientDriverStatement(base);
    }

    /**
     * Adds an expectation on the ClientDriver to expect the given request and response.
     * 
     * @param request The request to expect
     * @param response The response to expect
     */
    public void addExpectation(ClientDriverRequest request, ClientDriverResponse response) {
        clientDriver.addExpectation(request, response);
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
     * Statement which evaluates the given Statement and shuts down the client after evaluation.
     */
    private class ClientDriverStatement extends Statement {

        private final Statement statement;

        public ClientDriverStatement(Statement statement) {
            this.statement = statement;
        }

        @Override
        public void evaluate() throws Throwable {
            statement.evaluate();
            clientDriver.shutdown();
        }

    }

}
