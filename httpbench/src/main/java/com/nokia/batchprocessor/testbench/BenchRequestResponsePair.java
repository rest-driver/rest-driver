package com.nokia.batchprocessor.testbench;

/**
 * Trivial little bean for pairing an request with its response.
 * 
 * @author mjg
 * 
 */
public class BenchRequestResponsePair {

    private final BenchRequest  request;
    private final BenchResponse response;

    /**
     * Constructor.
     * 
     * @param request
     *            The {@link BenchRequest}
     * @param response
     *            The {@link BenchResponse}
     */
    public BenchRequestResponsePair(final BenchRequest request, final BenchResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * @return the response
     */
    public BenchResponse getResponse() {
        return response;
    }

    /**
     * @return the request
     */
    public BenchRequest getRequest() {
        return request;
    }

}
