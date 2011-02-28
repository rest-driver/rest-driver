package com.nokia.batchprocessor.testbench;

import org.mortbay.jetty.Handler;

/**
 * 
 * Interface for classes which handle incoming http requests in the Bench
 * Tester.
 * 
 * @author mjg
 * 
 */
public interface BenchHandler {

    /**
     * Add in a {@link BenchRequest}/{@link BenchResponse} pair.
     * 
     * @param request
     *            The expected request
     * @param response
     *            The response to serve to that request
     * 
     */
    void addExpectation(BenchRequest request, BenchResponse response);

    /**
     * This method will throw a {@link BenchRuntimeException} if there have been
     * any unexpected requests.
     */
    void checkForUnexpectedRequests();

    /**
     * This method will throw a {@link BenchRuntimeException} if any
     * expectations have not been met.
     */
    void checkForUnmatchedExpectations();

    /**
     * Get this object as a Jetty Handler. Call this if you have a reference to
     * it as a {@link BenchHandler} only.
     * 
     * @return "this", usually.
     */
    Handler getJettyHandler();

}
