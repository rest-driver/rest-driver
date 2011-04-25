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
package com.github.restdriver.clientdriver;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for classes whose responsibility is to match incoming Http requests with expected BenchRequests.
 * 
 * @author mjg
 * 
 */
public interface RequestMatcher {

    /**
     * Checks for a match between an actual {@link HttpServletRequest} and an expected {@link ClientDriverRequest}.
     * Implementations can be as precise or as loose as they like when matching.
     * 
     * @param actualRequest
     *            The actual request
     * @param expectedRequest
     *            The expected {@link ClientDriverRequest}
     * @return True if there is a match, falsetto otherwise.
     */
    boolean isMatch(HttpServletRequest actualRequest, ClientDriverRequest expectedRequest);

}
