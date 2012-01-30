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

import com.github.restdriver.RestDriverProperties;
import com.github.restdriver.serverdriver.RestServerDriver;

/**
 * Wraps an {@link HttpUriRequest} with some other details which that class does not support.
 */
public final class ServerDriverHttpUriRequest {
    
    private static final String USER_AGENT = "User-Agent";
    private static final String DEFAULT_USER_AGENT = "rest-server-driver/" + RestDriverProperties.getVersion();
    
    private final HttpUriRequest request;
    
    private HttpHost proxyHost;
    private long connectionTimeout = RestServerDriver.DEFAULT_CONNECTION_TIMEOUT;
    private long socketTimeout = RestServerDriver.DEFAULT_SOCKET_TIMEOUT;
    
    /**
     * Constructor.
     * 
     * @param request The {@link HttpUriRequest} to wrap.
     */
    public ServerDriverHttpUriRequest(HttpUriRequest request) {
        this.request = request;
        this.request.setHeader(USER_AGENT, DEFAULT_USER_AGENT);
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
    
    /**
     * Getter.
     * 
     * @return The connection timeout.
     */
    public long getConnectionTimeout() {
        return connectionTimeout;
    }
    
    /**
     * Set the number of milliseconds to use as a connection timeout.
     * 
     * @param connectionTimeout The number of milliseconds to use as the timeout.
     */
    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    /**
     * Getter.
     * 
     * @return The socket timeout.
     */
    public long getSocketTimeout() {
        return socketTimeout;
    }
    
    /**
     * Set the number of milliseconds to use as a socket timeout.
     * 
     * @param socketTimeout The number of milliseconds to use as the timeout.
     */
    public void setSocketTimeout(long socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
    
}
