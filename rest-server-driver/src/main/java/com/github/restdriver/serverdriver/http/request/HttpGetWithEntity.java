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

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * A version of the HTTP GET method which accepts an entity
 */
public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
    
    /**
     * The HTTP method name.
     */
    public final static String METHOD_NAME = "GET";
    
    /**
     * Creates a new instance of this request.
     * 
     * @param uri The URI this request will be made to.
     */
    public HttpGetWithEntity(String uri) {
        super();
        setURI(URI.create(uri));
    }
    
    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
    
}
