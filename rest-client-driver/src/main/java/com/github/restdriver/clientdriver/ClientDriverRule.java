package com.github.restdriver.clientdriver;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * The ClientDriverRule allows a user to specify expectations on the HTTP requests that are made against it.
 */
public final class ClientDriverRule implements MethodRule {

    private final ClientDriver clientDriver = new ClientDriverFactory().createClientDriver();

    /**
     * A Rule that expects no requests to be called on the client driver.
     *  
     * @return The rule
     */
    public static ClientDriverRule none() {
        return new ClientDriverRule();
    }

    private ClientDriverRule() {
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
    public void expect(ClientDriverRequest request, ClientDriverResponse response) {
        clientDriver.addExpectation(request, response);
    }

    /**
     * The base URL of the underlying ClientDriver.
     * 
     * @return The base URL String
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
