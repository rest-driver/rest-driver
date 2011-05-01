/**
 * Copyright © 2010-2011 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.restdriver.serverdriver.http.response;

import static org.apache.commons.lang.StringUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.restdriver.serverdriver.Json;
import com.github.restdriver.serverdriver.Xml;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.github.restdriver.serverdriver.http.Header;
import org.codehaus.jackson.JsonNode;
import org.w3c.dom.Element;

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
     *
     * @param response     the HttpResponse
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
    public List<Header> getHeaders(String headerName) {

        List<Header> matchingHeaders = new ArrayList<Header>();

        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase(headerName)) {
                matchingHeaders.add(header);
            }
        }

        return matchingHeaders;
    }

    @Override
    public Header getHeader(String headerName) {

        List<Header> matchingHeaders = getHeaders(headerName);

        if (matchingHeaders.isEmpty()) {
            return null;
        }

        if (matchingHeaders.size() > 1) {
            throw new IllegalStateException("Attempt to get single header '" + headerName + "' but more than one value found.");
        }

        return matchingHeaders.get(0);
    }

    @Override
    public long getResponseTime() {
        return responseTime;
    }

    @Override
    public JsonNode asJson() {
        return Json.asJson(this);
    }

    @Override
    public Element asXml() {
        return Xml.asXml(this.getContent());
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
