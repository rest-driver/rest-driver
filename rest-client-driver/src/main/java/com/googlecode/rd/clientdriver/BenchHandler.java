package com.googlecode.rd.clientdriver;

import org.eclipse.jetty.server.Handler;

import com.googlecode.rd.types.ClientDriverRequest;
import com.googlecode.rd.types.ClientDriverResponse;

/**
 * Interface for classes which handle incoming http requests in the Client Driver
 */
public interface BenchHandler {

    /**
     * Add in a {@link ClientDriverRequest}/{@link ClientDriverResponse} pair.
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
     * Get this object as a Jetty Handler. Call this if you have a reference to it as a {@link BenchHandler} only.
     * 
     * @return "this", usually.
     */
    Handler getJettyHandler();

}
