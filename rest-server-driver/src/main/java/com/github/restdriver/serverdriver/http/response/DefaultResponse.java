package com.github.restdriver.serverdriver.http.response;

import static org.apache.commons.lang.StringUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.github.restdriver.serverdriver.http.Header;

/**
 * Our class which describes an HTTP response.
 */
public final class DefaultResponse implements Response {

    private final int statusCode;
    private final String content;
    private final List<Header> headers;
    private final long responseTime;

    /**
     * Constructor from apache HttpResponse.
     * @param response the HttpResponse
     * @param responseTime time taken for the request in milliseconds
     */
    public DefaultResponse(HttpResponse response, long responseTime) {
        this.statusCode = response.getStatusLine().getStatusCode();
        this.content = contentFromResponse(response);
        this.headers = headersFromResponse(response);
        this.responseTime = responseTime;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public List<Header> getHeaders() {
        return headers;
    }

    @Override
    public long getResponseTime() {
        return responseTime;
    }

    @Override
    public String toString() {
        return "status=" + statusCode + "|content=" + content + "|headers=[" + join(headers, ",") + "]";
    }

    private static String contentFromResponse(HttpResponse response) {
        InputStream stream = null;
        String content;

        try {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                content = null;
            } else {
                stream = response.getEntity().getContent();
                content = IOUtils.toString(stream, "UTF-8");
                // TODO: Examine HTTP headers in case of different encoding.
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting response entity", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return content;
    }

    private static List<Header> headersFromResponse(HttpResponse response) {
        List<Header> headers = new ArrayList<Header>();

        for (org.apache.http.Header currentHeader : response.getAllHeaders()) {
            Header header = new Header(currentHeader.getName(), currentHeader.getValue());
            headers.add(header);
        }

        return headers;
    }

}
