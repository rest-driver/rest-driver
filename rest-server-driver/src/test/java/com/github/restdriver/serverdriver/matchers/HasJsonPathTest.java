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

import java.text.ParseException;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.StringDescription;
import org.junit.Test;

import com.github.restdriver.serverdriver.Json;

/**
 * User: mjg
 * Date: 07/05/11
 * Time: 22:21
 */
public class HasJsonPathTest {

    private HasJsonPath<?> hasJsonPath;

    @Test
    public void jsonMatchesString() {
        JsonNode json = Json.asJson(makeJson("{'foo': 'bar'}"));

        hasJsonPath = new HasJsonPath<String>("$.foo", is("bar"));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    @Test
    public void jsonMatchesLong() {
        JsonNode json = Json.asJson(makeJson("{'foo': 5}"));

        hasJsonPath = new HasJsonPath<Long>("$.foo", greaterThan(4L));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    @Test
    public void jsonMatchesInteger() {
        JsonNode json = Json.asJson(makeJson("{'foo': 5}"));

        hasJsonPath = new HasJsonPath<Integer>("$.foo", is(5));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    @Test
    public void wrongClassIsCoercedCorrectly() {
        JsonNode json = Json.asJson(makeJson("{'foo': 5}"));

        hasJsonPath = new HasJsonPath<Integer>("$.foo", greaterThan(4)); // jp returns Long
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    @Test(expected = RuntimeJsonTypeMismatchException.class)
    public void outOfIntegerRangeNumberThrowsException() throws ParseException {
        JsonNode json = Json.asJson(makeJson("{'foo': 4294967294 }")); // too big

        hasJsonPath = new HasJsonPath<Integer>("$.foo", greaterThan(4));
        hasJsonPath.matchesSafely(json);
    }

    @Test(expected = RuntimeJsonTypeMismatchException.class)
    public void matchingADoubleAndAnInt() throws ParseException {
        JsonNode json = Json.asJson(makeJson("{'foo': 5.5 }")); // too big

        hasJsonPath = new HasJsonPath<Integer>("$.foo", greaterThan(4));
        hasJsonPath.matchesSafely(json);
    }

    @Test
    public void testTypeIsTotallyWrong() {
        JsonNode json = Json.asJson(makeJson("{'foo': 5}"));

        hasJsonPath = new HasJsonPath<String>("$.foo", containsString("no it doesn't"));
        assertThat(hasJsonPath.matchesSafely(json), is(false));
    }

    @Test
    public void jsonMatchesFloat() {
        JsonNode json = Json.asJson(makeJson("{'foo': 5.5}"));

        hasJsonPath = new HasJsonPath<Double>("$.foo", is(5.5));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    @Test
    public void jsonMatchesBoolean() {
        JsonNode json = Json.asJson(makeJson("{'foo': false}"));

        hasJsonPath = new HasJsonPath<Boolean>("$.foo", is(true));
        assertThat(hasJsonPath.matchesSafely(json), is(false));
    }

    @Test
    public void jsonMatchesNull() {
        JsonNode json = Json.asJson(makeJson("{'foo': null}"));

        hasJsonPath = new HasJsonPath<Object>("$.foo", is(nullValue()));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    @Test
    public void matcherMatchesPresentFieldValue() {
        JsonNode json = Json.asJson(makeJson("{'foo': 23}"));

        hasJsonPath = new HasJsonPath<Object>("$.foo");
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    @Test
    public void matcherDoesntMatchMissingFieldValue() {
        JsonNode json = Json.asJson(makeJson("{'bar': 23}"));

        hasJsonPath = new HasJsonPath<Object>("$.foo");
        assertThat(hasJsonPath.matchesSafely(json), is(false));
    }

    @Test
    public void describeToDoesntThrowNPE(){
        // bugfix for issue #47

        hasJsonPath = new HasJsonPath<Object>("$.foo");
        StringDescription sd = new StringDescription();
        hasJsonPath.describeTo(sd);

        assertThat(sd.toString(), is("a JSON object matching JSONpath \"$.foo\""));

    }

    @Test
    public void matcherFailsIfJsonPathMatchesNoItemsInArray() {

    	hasJsonPath = new HasJsonPath<Object>("$.foo");
    	
    	final JsonNode matchingJson = Json.asJson(makeJson("[ { 'foo': 1 } ]"));
    	final JsonNode nonMatchingJson = Json.asJson(makeJson("[ { 'bar': 1 } ]"));    	
    	
    	assertThat(hasJsonPath.matchesSafely(matchingJson), is(true));
    	assertThat(hasJsonPath.matchesSafely(nonMatchingJson), is(false));    	
    }
    
    private String makeJson(String fakeJson) {
        return fakeJson.replace("'", "\"");
    }

}
