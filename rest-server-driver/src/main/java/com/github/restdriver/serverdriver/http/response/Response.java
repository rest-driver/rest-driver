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

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.w3c.dom.Element;

import com.github.restdriver.serverdriver.http.Header;

/**
 * Encapsulates a response from an HTTP server.
 */
public interface Response {

    /**
     * The maximum number of response body characters to display. 
     */
    int MAX_BODY_DISPLAY_LENGTH = 1024;

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
     * Get the all headers with the specified name.
     *
     * @param headerName The name of the header
     * @return The Headers associated with that name.  Possibly an empty list.
     */
    List<Header> getHeaders(String headerName);

    /**
     * Get the value of an individual Header.  If there are multiple values for the same header, this
     * method will return the first one found.  If you expect there to be multiple values for the same header
     * then you should use {@link #getHeaders(String)}.
     *
     * @param headerName The name of the header
     * @return The Header associated with that name.  Possibly null.
     */
    Header getHeader(String headerName);

    /**
     * How long did the response take?
     *
     * @return Round-trip response time in milliseconds
     */
    long getResponseTime();

    /**
     * Returns the JSON response content as a JsonNode, or throws RuntimeMappingException.
     *
     * @return The JsonNode
     */
    JsonNode asJson();

    /**
     * Returns the XML response content as an org.w3c.Element, or throws RuntimeMappingException.
     *
     * @return The Element
     */
    Element asXml();

    /**
     * Returns the response as an HTTP-style String.
     * 
     * @return The String
     */
    String toHttpString();

}
