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

import java.text.ParseException;

/**
 * Runtime wrapper for {@link java.text.ParseException}. caused by badly-formatted dates.
 * <p/>
 * User: mjg Date: 14/05/11 Time: 21:52
 */
public class RuntimeDateFormatException extends RuntimeException {
    
    /**
     * Constructor which takes the original ParseException.
     * 
     * @param pe The original ParseException.
     */
    public RuntimeDateFormatException(ParseException pe) {
        super(pe);
    }
    
}
