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
package com.github.restdriver.clientdriver;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for encapsulating an HTTP response.
 */
public final class ClientDriverResponse {

    private static final int DEFAULT_STATUS_CODE = 200;

    private int status;
    private final String content;
    private String contentType;
    private final Map<String, String> headers;

    /**
     * Creates a new response with an empty body, a status code of 200 and a Content-Type of 'text/plain'.
     */
    public ClientDriverResponse() {
        this("");
    }

    /**
     * Creates a new response with the given body, a status code of 200 and a Content-Type of 'text/plain'.
     * 
     * @param content The content of the response
     */
    public ClientDriverResponse(String content) {
        this.content = content;
        status = DEFAULT_STATUS_CODE;
        contentType = "text/plain";
        headers = new HashMap<String, String>();
    }

    /**
     * @return The content, or an empty string if the content is null.
     */
    public String getContent() {
        if (content == null) {
            return "";
        } else {
            return content;
        }
    }

    /**
     * @param withStatus
     *            the status to set
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverResponse withStatus(int withStatus) {
        status = withStatus;
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
     * @param withContentType
     *            the contentType to set
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverResponse withContentType(String withContentType) {
        this.contentType = withContentType;
        return this;
    }

    /**
     * Set headers on the response.
     * 
     * @param name
     *            The header name
     * @param value
     *            The header value
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverResponse withHeader(String name, String value) {
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
