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
package com.github.restdriver.serverdriver.http;

import static org.apache.commons.lang.StringUtils.*;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * Encapsulates a Request body for a method.
 */
public final class RequestBody implements AnyRequestModifier {

    private static final String DEFAULT_CONTENT_ENCODING = "UTF-8";

    private final String content;
    private final String contentType;

    /**
     * Creates a new request body instance.
     * 
     * @param content
     *            A string to use for the content
     * @param contentType
     *            A string representing the content-type
     */
    public RequestBody(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    /**
     * Gets the content of this request body.
     * 
     * @return The content as a string
     * 
     * @deprecated This shouldn't need to be exposed. Expect it to go away in
     *             the future.
     */
    @Deprecated
    public String getContent() {
        return content;
    }

    /**
     * Gets the content-type of this request body.
     * 
     * @return The content-type as a string
     * 
     * @deprecated This shouldn't need to be exposed. Expect it to go away in
     *             the future.
     */
    @Deprecated
    public String getContentType() {
        return contentType;
    }

    @Override
    public void applyTo(ServerDriverHttpUriRequest request) {

        HttpUriRequest internalRequest = request.getHttpUriRequest();

        if (!(internalRequest instanceof HttpEntityEnclosingRequest)) {
            return;
        }

        HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) internalRequest;

        entityRequest.setHeader("Content-type", contentType);
        entityRequest.setEntity(new StringEntity(content, createContentType(contentType)));

    }

    private ContentType createContentType(String contentType) {
        try {

            MimeType mimeType = new MimeType(contentType);

            String mediaType = mimeType.getBaseType();
            String charset = defaultString(mimeType.getParameter("charset"), DEFAULT_CONTENT_ENCODING);

            return ContentType.create(mediaType, charset);

        } catch (MimeTypeParseException e) {
            throw new IllegalArgumentException("Invalid content type: " + contentType, e);
        }
    }

}
