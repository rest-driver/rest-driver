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

public class RequestTimeout implements AnyRequestModifier {
    
    private final long connectionTimeout;
    private final long socketTimeout;
    
    /**
     * Constructor.
     * 
     * @param connectionTimeout The connection timeout.
     * @param socketTimeout The socket timeout.
     */
    public RequestTimeout(long connectionTimeout, long socketTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
    }
    
    @Override
    public void applyTo(ServerDriverHttpUriRequest request) {
        request.setConnectionTimeout(connectionTimeout);
        request.setSocketTimeout(socketTimeout);
    }
    
}
