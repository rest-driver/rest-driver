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
package com.github.restdriver.matchers;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.exception.RuntimeAssertionFailure;

public class HasJsonWhichTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void matcherShouldDescribeItselfCorrectly() {
        HasJsonWhich matcher = new HasJsonWhich(new HasJsonValue("number", new IsEqual<Integer>(1)));
        Description description = new StringDescription();
        matcher.describeTo(description);
        assertThat(description.toString(), is("JsonNode with 'number' matching: <1>"));
    }
    
    @Test
    public void matcherShouldDescribeMismatchCorrectly() {
        HasJsonWhich matcher = new HasJsonWhich(new HasJsonValue("number", new IsEqual<Integer>(1)));
        Description description = new StringDescription();
        matcher.describeMismatchSafely("{\"number\":10}", description);
        assertThat(description.toString(), is("was <{\"number\":10}>"));
    }
    
    @Test
    public void matcherShouldMatchACorrectJsonString() {
        HasJsonWhich matcher = new HasJsonWhich(new HasJsonValue("number", new IsEqual<Integer>(1)));
        assertThat(matcher.matches("{\"number\":1}"), is(true));
    }
    
    @Test
    public void matcherShouldNotMatchAnIncorrectJsonString() {
        HasJsonWhich matcher = new HasJsonWhich(new HasJsonValue("number", new IsEqual<Integer>(1)));
        assertThat(matcher.matches("{\"number\":10}"), is(false));
    }
    
    @Test
    public void matcherShouldThrowAnExceptionIfGivenInvalidJson() {
        thrown.expect(RuntimeAssertionFailure.class);
        HasJsonWhich matcher = new HasJsonWhich(new HasJsonValue("number", new IsEqual<Integer>(1)));
        matcher.matches("{number\":10}");
    }
    
}
