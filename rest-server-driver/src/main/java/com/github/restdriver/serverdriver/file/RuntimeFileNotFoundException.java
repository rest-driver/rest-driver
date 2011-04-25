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
package com.github.restdriver.serverdriver.file;

import java.io.FileNotFoundException;

/**
 * Runtime wrapper for {@link FileNotFoundException}.
 * 
 * User: mjg
 * Date: 21/04/11
 * Time: 12:08
 */
public class RuntimeFileNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3651887477900227630L;

    /**
     * Creates a new instance of the exception.
     * 
     * @param fnfe The exception to use as the cause
     */
    public RuntimeFileNotFoundException(FileNotFoundException fnfe) {
        super(fnfe);
    }

}
