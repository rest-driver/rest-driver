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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Matcher to check that an HttpResponse has the specified body.
 */
public final class HasResponseBody extends TypeSafeMatcher<Response> {
    
    private final Matcher<String> bodyMatcher;
    
    /**
     * Creates a new instance of this matcher.
     * 
     * @param bodyMatcher The matcher to be used to evaluate the body of the response
     */
    public HasResponseBody(Matcher<String> bodyMatcher) {
        this.bodyMatcher = bodyMatcher;
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText("Response with body matching: ");
        bodyMatcher.describeTo(description);
    }
    
    @Override
    public boolean matchesSafely(Response actualResponse) {
        
        String actualContent = actualResponse.getContent();
        
        return bodyMatcher.matches(actualContent);
    }
    
}
