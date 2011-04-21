package com.github.restdriver.clientdriver;

/**
 * Pairs an expected request with its response.
 */
public final class ClientDriverRequestResponsePair {

    private final ClientDriverRequest request;
    private final ClientDriverResponse response;

    /**
     * Constructor.
     * 
     * @param request
     *            The {@link ClientDriverRequest}
     * @param response
     *            The {@link ClientDriverResponse}
     */
    public ClientDriverRequestResponsePair(ClientDriverRequest request, ClientDriverResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * @return the response
     */
    public ClientDriverResponse getResponse() {
        return response;
    }

    /**
     * @return the request
     */
    public ClientDriverRequest getRequest() {
        return request;
    }

}
