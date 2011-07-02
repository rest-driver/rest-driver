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
/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.github.restdriver.serverdriver.http;

import org.apache.http.HttpHost;

/**
 * Encapsulates a request to use an HTTP proxy.
 */
public final class RequestProxy implements AnyRequestModifier {

    private final HttpHost proxyHost;
    
    /**
     * Constructor.
     * 
     * @param proxyHost The proxy host.
     * @param proxyPort The proxy port.
     */
    public RequestProxy(String proxyHost, int proxyPort) {
        this.proxyHost = new HttpHost(proxyHost, proxyPort);
    }

    @Override
    public void applyTo(ServerDriverHttpUriRequest request) {
        // TODO: I don't think this works.  Documentation is SHIT
        // but I think this has to be set on the client, not the request.
        // Stupid untyped bag of parameters pattern.
        request.setProxyHost(proxyHost);
    }

}
