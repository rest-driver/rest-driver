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

import org.apache.commons.codec.binary.Base64;

/**
 * Modifies a request by adding an HTTP basic authorization header.
 */
public class BasicAuth implements AnyRequestModifier {
    
    private final String username;
    private final String password;
    
    /**
     * Creates a new instance of the class.
     * 
     * @param username The username.
     * @param password The password.
     */
    public BasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    @Override
    public void applyTo(ServerDriverHttpUriRequest request) {
        request.getHttpUriRequest().addHeader("Authorization", "Basic " + base64(username, password));
    }
    
    private static String base64(String username, String password) {
        return new String(Base64.encodeBase64((username + ":" + password).getBytes()));
    }
    
}
