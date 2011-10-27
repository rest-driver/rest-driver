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
package com.github.restdriver.serverdriver.matchers;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Matcher to assert on the headers in an HTTP response.
 */
public final class HasHeader extends TypeSafeMatcher<Response> {
    
    private final String name;
    
    /**
     * Creates an instance of this matcher.
     * 
     * @param name The name of the header to check for the presence of
     */
    public HasHeader(String name) {
        this.name = name;
    }
    
    @Override
    protected boolean matchesSafely(Response response) {
        
        for (Header header : response.getHeaders()) {
            if (StringUtils.equalsIgnoreCase(header.getName(), name)) {
                return true;
            }
        }
        
        return false;
        
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText("Response with header named '" + name + "'");
    }
    
    @Override
    protected void describeMismatchSafely(Response response, Description mismatchDescription) {
        List<Header> headers = response.getHeaders();
        
        if (headers.isEmpty()) {
            mismatchDescription.appendText("Response has no headers");
        } else {
            mismatchDescription.appendText("Response has headers [" + StringUtils.join(response.getHeaders(), ", ") + "]");
        }
    }
    
}
