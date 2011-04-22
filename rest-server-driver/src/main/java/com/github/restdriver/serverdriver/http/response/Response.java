package com.github.restdriver.serverdriver.http.response;

import java.util.List;

import com.github.restdriver.serverdriver.http.Header;

/**
 * Encapsulates a response from an HTTP server.
 */
public interface Response {

    /**
     * What was the response code?
     *
     * @return the HTTP status code of the response
     */
    int getStatusCode();

    /**
     * What was the content of the response?
     *
     * @return The response body content as a string
     */
    String getContent();

    /**
     * What headers did the server send?
     *
     * @return A list of headers
     */
    List<Header> getHeaders();

    /**
     * How long did the response take?
     * 
     * @return Round-trip response time in milliseconds
     */
    long getResponseTime();

}
