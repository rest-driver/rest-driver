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
package com.github.restdriver.serverdriver.http.exception;

import java.net.URISyntaxException;

/**
 * RuntimeException wrapper for URISyntaxException.
 * 
 * User: mjg
 * Date: 08/05/11
 * Time: 14:59
 */
public class RuntimeUriSyntaxException extends RuntimeException {
    
    private static final long serialVersionUID = -8503822588074534959L;
    
    /**
     * Constructor.
     * 
     * @param message The reason for the exception.
     * @param e The underlying URISyntaxException.
     */
    public RuntimeUriSyntaxException(String message, URISyntaxException e) {
        super(message, e);
    }
}
