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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

import com.github.restdriver.serverdriver.http.response.Response;

public class HasStatusCodeTest {

    private Response response;
    private HasStatusCode matcher;

    @Before
    public void before() {
        response = mock(Response.class);
        matcher = new HasStatusCode(equalTo(200));
    }

    @Test
    public void matchesCorrectly() {

        when(response.getStatusCode()).thenReturn(200);
        assertThat(matcher.matches(response), is(true));

        when(response.getStatusCode()).thenReturn(201);
        assertThat(matcher.matches(response), is(false));

    }

    @Test
    public void matcherUsesProvidedMatcher() {

        matcher = new HasStatusCode(greaterThan(200));

        when(response.getStatusCode()).thenReturn(206);
        assertThat(matcher.matches(response), is(true));

    }

    @Test
    public void descriptionIsSufficient() {

        Description description = new StringDescription();

        matcher.describeTo(description);

        assertThat(description.toString(), is("Response with status code matching: <200>"));

    }

    @Test
    public void mismatchResponseWithNoBodyDescribesCorrectly() {

        when(response.getStatusCode()).thenReturn(200);
        when(response.getContent()).thenReturn(null);

        Description description = new StringDescription();

        matcher.describeMismatchSafely(response, description);

        assertThat(description.toString(), is("Response has status code: 200, and an empty body"));

    }

    @Test
    public void mismatchResponseWithBodyDescribesCorrectly() {

        when(response.getStatusCode()).thenReturn(200);
        when(response.getContent()).thenReturn("{}");

        Description description = new StringDescription();

        matcher.describeMismatchSafely(response, description);

        assertThat(description.toString(), is("Response has status code: 200, and body: {}"));

    }

}
