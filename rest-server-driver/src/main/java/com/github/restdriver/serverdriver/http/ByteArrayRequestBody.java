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

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;

/**
 * Encapsulates a request body created from an array of bytes.
 */
public class ByteArrayRequestBody implements BodyableRequestModifier {
    
    private final byte[] content;
    private final String contentType;
    
    /**
     * Creates a new body instance
     * 
     * @param content The body content as a byte array.
     * @param contentType The body content type.
     */
    public ByteArrayRequestBody(byte[] content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }
    
    @Override
    public void applyTo(ServerDriverHttpUriRequest request) {
        
        HttpUriRequest internalRequest = request.getHttpUriRequest();
        
        if (!(internalRequest instanceof HttpEntityEnclosingRequest)) {
            return;
        }
        
        HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) internalRequest;
        
        entityRequest.setHeader("Content-type", contentType);
        
        ByteArrayEntity entity = new ByteArrayEntity(content);
        entity.setContentType(contentType);
        entityRequest.setEntity(entity);
        
    }
    
}
