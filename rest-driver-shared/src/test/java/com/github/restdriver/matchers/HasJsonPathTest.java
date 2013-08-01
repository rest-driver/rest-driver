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

import java.io.IOException;
import java.text.ParseException;

import org.hamcrest.StringDescription;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restdriver.exception.RuntimeJsonTypeMismatchException;

/**
 * User: mjg
 * Date: 07/05/11
 * Time: 22:21
 */
public class HasJsonPathTest {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    private HasJsonPath<?> hasJsonPath;
    
    @Test
    public void jsonMatchesString() {
        JsonNode json = makeJson("{'foo': 'bar'}");
        
        hasJsonPath = new HasJsonPath<String>("$.foo", is("bar"));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonMatchesLong() {
        JsonNode json = makeJson("{'foo': 5}");
        
        hasJsonPath = new HasJsonPath<Long>("$.foo", greaterThan(4L));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonMatchesInteger() {
        JsonNode json = makeJson("{'foo': 5}");
        
        hasJsonPath = new HasJsonPath<Integer>("$.foo", is(5));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }
    
    @Test
    public void wrongClassIsCoercedCorrectly() {
        JsonNode json = makeJson("{'foo': 5}");
        
        hasJsonPath = new HasJsonPath<Integer>("$.foo", greaterThan(4)); // jp returns Long
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }
    
    @Test(expected = RuntimeJsonTypeMismatchException.class)
    public void outOfIntegerRangeNumberThrowsException() throws ParseException {
        JsonNode json = makeJson("{'foo': 4294967294 }"); // too big
        
        hasJsonPath = new HasJsonPath<Integer>("$.foo", greaterThan(4));
        hasJsonPath.matchesSafely(json);
    }
    
    @Test(expected = RuntimeJsonTypeMismatchException.class)
    public void matchingADoubleAndAnInt() throws ParseException {
        JsonNode json = makeJson("{'foo': 5.5 }"); // too big
        
        hasJsonPath = new HasJsonPath<Integer>("$.foo", greaterThan(4));
        hasJsonPath.matchesSafely(json);
    }
    
    @Test
    public void testTypeIsTotallyWrong() {
        JsonNode json = makeJson("{'foo': 5}");
        
        hasJsonPath = new HasJsonPath<String>("$.foo", containsString("no it doesn't"));
        assertThat(hasJsonPath.matchesSafely(json), is(false));
    }
    
    @Test
    public void jsonMatchesFloat() {
        JsonNode json = makeJson("{'foo': 5.5}");
        
        hasJsonPath = new HasJsonPath<Double>("$.foo", is(5.5));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonMatchesBoolean() {
        JsonNode json = makeJson("{'foo': false}");
        
        hasJsonPath = new HasJsonPath<Boolean>("$.foo", is(true));
        assertThat(hasJsonPath.matchesSafely(json), is(false));
    }
    
    @Test
    public void jsonMatchesNull() {
        JsonNode json = makeJson("{'foo': null}");
        
        hasJsonPath = new HasJsonPath<Object>("$.foo", is(nullValue()));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }
    
    @Test
    public void matcherMatchesPresentFieldValue() {
        JsonNode json = makeJson("{'foo': 23}");
        
        hasJsonPath = new HasJsonPath<Object>("$.foo");
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }
    
    @Test
    public void matcherDoesntMatchMissingFieldValue() {
        JsonNode json = makeJson("{'bar': 23}");
        
        hasJsonPath = new HasJsonPath<Object>("$.foo");
        assertThat(hasJsonPath.matchesSafely(json), is(false));
    }
    
    @Test
    public void matcherDoesntMatchWithinObjectsInArrayWhenGivenFieldName() {
        JsonNode json = makeJson("{'foo': [{'id': 1}, {'id': 2}, {'id': 3}]}");
        
        hasJsonPath = new HasJsonPath<Object>("$.foo.id");
        assertThat(hasJsonPath.matchesSafely(json), is(false));
    }
    
    @Test
    public void matcherMatchesWithinObjectsWhenUsingWildcardArrayMatch() {
        JsonNode json = makeJson("{'foo': [{'id': 1}, {'id': 2}, {'id': 3}]}");
        
        hasJsonPath = new HasJsonPath<Object>("$.foo[*].id");
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }
    
    @Test
    public void describeToDoesntThrowNPE() {
        // bugfix for issue #47
        
        hasJsonPath = new HasJsonPath<Object>("$.foo");
        StringDescription sd = new StringDescription();
        hasJsonPath.describeTo(sd);
        
        assertThat(sd.toString(), is("a JSON object matching JSONpath \"$.foo\""));
        
    }
    
    private JsonNode makeJson(String fakeJson) {
        try {
            return MAPPER.readTree(fakeJson.replace("'", "\""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
