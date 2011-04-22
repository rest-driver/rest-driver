package com.github.restdriver.clientdriver.jetty;

import org.eclipse.jetty.server.Handler;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;

/**
 * Interface for classes which handle incoming http requests in the Client Driver.
 */
public interface ClientDriverJettyHandler {

    /**
     * Add in a {@link com.github.restdriver.clientdriver.ClientDriverRequest}/{@link com.github.restdriver.clientdriver.ClientDriverResponse} pair.
     * 
     * @param request
     *            The expected request
     * @param response
     *            The response to serve to that request
     * 
     */
    void addExpectation(ClientDriverRequest request, ClientDriverResponse response);

    /**
     * This method will throw a ClientDriverFailedExpectationException if there have been any unexpected requests.
     */
    void checkForUnexpectedRequests();

    /**
     * This method will throw a ClientDriverFailedExpectationException if any expectations have not been met.
     */
    void checkForUnmatchedExpectations();

    /**
     * Get this object as a Jetty Handler. Call this if you have a reference to it as a {@link ClientDriverJettyHandler} only.
     * 
     * @return "this", usually.
     */
    Handler getJettyHandler();

}
