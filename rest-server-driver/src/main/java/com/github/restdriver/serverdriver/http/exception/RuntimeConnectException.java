/**
 * Copyright © 2010-2011 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.restdriver.serverdriver.http.exception;

import java.net.ConnectException;

/**
 * Runtime wrapper for {@link ConnectException}.
 * 
 * User: mjg
 * Date: 21/04/11
 * Time: 16:33
 */
public class RuntimeConnectException extends RuntimeException {

    private static final long serialVersionUID = -8998669008887196143L;

    /**
     * Create a new instance of the exception with a given cause.
     *
     * @param ce
     *            The exception to use as the cause
     */
    public RuntimeConnectException(ConnectException ce) {
        super(ce);
    }
}
