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

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Wraps an {@link HttpUriRequest} with some other details which that class does not support.
 */
public final class ServerDriverHttpUriRequest {
    
    private final HttpUriRequest request;
    
    private HttpHost proxyHost;
    
    /**
     * Constructor.
     * 
     * @param request The {@link HttpUriRequest} to wrap.
     */
    public ServerDriverHttpUriRequest(HttpUriRequest request) {
        this.request = request;
    }
    
    /**
     * Get the wrapped {@link HttpUriRequest}.
     * 
     * @return The wrapped {@link HttpUriRequest}.
     */
    public HttpUriRequest getHttpUriRequest() {
        return request;
    }
    
    /**
     * Set the host details to use as a proxy.
     * 
     * @param proxyHost The {@link HttpHost} to use as a proxy.
     */
    public void setProxyHost(HttpHost proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    /**
     * Getter.
     * 
     * @return The {@link HttpHost} to be used as a proxy.
     */
    public HttpHost getProxyHost() {
        return proxyHost;
    }
    
}
