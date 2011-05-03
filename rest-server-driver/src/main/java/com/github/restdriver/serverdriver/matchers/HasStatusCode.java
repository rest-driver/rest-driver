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

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Matcher to check whether an HTTP response has a particular status code. 
 */
public final class HasStatusCode extends TypeSafeMatcher<Response> {

    private final Matcher<Integer> statusCodeMatcher;

    /**
     * Creates an instance of this matcher.
     * 
     * @param statusCodeMatcher The matcher to be used to evaluate the status code of a response
     */
    public HasStatusCode(Matcher<Integer> statusCodeMatcher) {
        this.statusCodeMatcher = statusCodeMatcher;
    }

    @Override
    protected boolean matchesSafely(Response item) {
        return statusCodeMatcher.matches(item.getStatusCode());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Response with status code matching: ");
        statusCodeMatcher.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(Response item, Description mismatchDescription) {
        mismatchDescription.appendText("Response has status code: " + item.getStatusCode() + ", and ");

        String content = item.getContent();

        if (StringUtils.isNotEmpty(content)) {
            mismatchDescription.appendText("body: " + StringUtils.abbreviate(content, Response.MAX_BODY_DISPLAY_LENGTH) + "");
        } else {
            mismatchDescription.appendText("an empty body");
        }
    }

}
