package com.googlecode.rd.clientdriver;

import com.googlecode.rd.types.ClientDriverRequest;
import com.googlecode.rd.types.ClientDriverResponse;

/**
 * Pairs an expected request with its response.
 */
public class ClientDriverRequestResponsePair {

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
    public ClientDriverRequestResponsePair(final ClientDriverRequest request, final ClientDriverResponse response) {
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
