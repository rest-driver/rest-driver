/**
 * Copyright ¬© 2010-2011 Nokia
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

public class HasResponseBodyTest {

    private HasResponseBody matcher;

    @Before
    public void before() {
        matcher = new HasResponseBody(containsString("something"));
    }

    @Test
    public void matcherDescribesItselfCorrectly() {
        Description description = new StringDescription();
        matcher.describeTo(description);

        assertThat(description.toString(), is("Response with body matching: a string containing \"something\""));
    }

    @Test
    public void matcherFailsWhenBodyDoesntMatch() {
        Response response = mock(Response.class);
        when(response.getContent()).thenReturn("This the response body, it doesn't contain anything");

        assertThat(matcher.matches(response), is(false));
    }

    @Test
    public void matcherPassesWhenBodyMatches() {
        Response response = mock(Response.class);
        when(response.getContent()).thenReturn("This the response body, it contains something");

        assertThat(matcher.matches(response), is(true));
    }

}
