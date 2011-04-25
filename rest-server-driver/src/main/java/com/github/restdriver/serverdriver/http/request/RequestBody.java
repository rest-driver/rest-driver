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
package com.github.restdriver.serverdriver.http.request;

/**
 * Encapsulates a Request body for a PUT or a POST.
 * 
 * User: mjg
 * Date: 21/04/11
 * Time: 14:32
 */
public final class RequestBody {

    private final String content;
    private final String contentType;

    /**
     * Creates a new request body instance.
     * 
     * @param content A string to use for the content
     * @param contentType A string representing the content-type
     */
    public RequestBody(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    /**
     * Gets the content of this request body.
     * 
     * @return The content as a string
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the content-type of this request body.
     * 
     * @return The content-type as a string
     */
    public String getContentType() {
        return contentType;
    }
}
