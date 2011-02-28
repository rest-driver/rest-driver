package com.nokia.batchprocessor.testbench;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Class for encapsulating a Response from the Bench Server.
 * 
 * @author mjg
 * 
 */
public class BenchResponse {

    private static final int          DEFAULT_RESPONSE = 200;

    private int                       status;
    private final String              content;
    private String                    contentType;
    private final Map<String, String> headers;

    /**
     * Constructor. The only mandatory argument is the content of the response.
     * For an empty response, use "". Defaults are:<br/>
     * <ul>
     * <li>Http status = 200</li>
     * <li>Content-Type = text/plain</li>
     * <li>Headers also include the Jetty defaults, which is just
     * "Server: Jetty 6.2.1"</li>
     * </ul>
     * 
     * @param content
     *            The mandatory argument.
     */
    public BenchResponse(final String content) {
        this.content = content;
        status = DEFAULT_RESPONSE;
        contentType = "text/plain";
        headers = new HashMap<String, String>();
    }

    /**
     * @return The content, or an empty string if the content is null.
     */
    public String getContent() {
        return (content == null ? "" : content);
    }

    /**
     * @param status
     *            the status to set
     * @return the object you called the method on, so you can chain these
     *         calls.
     */
    public BenchResponse withStatus(final int status) {
        this.status = status;
        return this;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType
     *            the contentType to set
     * @return the object you called the method on, so you can chain these
     *         calls.
     */
    public BenchResponse withContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Set headers on the response
     * 
     * @param name
     *            The header name
     * @param value
     *            The header value
     * @return the object you called the method on, so you can chain these
     *         calls.
     */
    public BenchResponse withHeader(final String name, final String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

}
