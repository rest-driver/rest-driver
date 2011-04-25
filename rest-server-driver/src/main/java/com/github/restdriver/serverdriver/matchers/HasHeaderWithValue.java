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
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Matcher to enable assertion on HTTP header values.
 * 
 * TODO: Is this class necessary we can do:
 * <p/>
 * assertThat(response.getHeaders(), hasItem(new Header("header", "value")))
 */
public final class HasHeaderWithValue extends TypeSafeMatcher<Response> {

    private final String name;
    private final Matcher<String> valueMatcher;

    /**
     * Creates a new instance of this matcher.
     * 
     * @param name The name of the header to evaluate
     * @param valueMatcher The matcher to use against the header value if a header with the specified name is found
     */
    public HasHeaderWithValue(String name, Matcher<String> valueMatcher) {
        this.name = name;
        this.valueMatcher = valueMatcher;
    }

    @Override
    protected boolean matchesSafely(Response response) {

        for (Header header : response.getHeaders()) {
            if (!StringUtils.equals(header.getName(), name)) {
                continue;
            }

            return valueMatcher.matches(header.getValue());
        }

        return false;

    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Response with header named '" + name + "' and value matching: ");
        valueMatcher.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(Response response, Description mismatchDescription) {
        List<Header> headers = response.getHeaders();

        if (headers.isEmpty()) {
            mismatchDescription.appendText("Response has no headers");
        } else {
            mismatchDescription.appendText("Response has headers [" + StringUtils.join(response.getHeaders(), ",") + "]");
        }
    }
}
