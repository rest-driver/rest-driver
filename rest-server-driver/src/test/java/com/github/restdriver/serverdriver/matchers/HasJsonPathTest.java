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

import com.github.restdriver.serverdriver.Json;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * User: mjg
 * Date: 07/05/11
 * Time: 22:21
 */
public class HasJsonPathTest {

    private HasJsonPath hasJsonPath;

    @Test
    public void jsonMatchesString() {
        JsonNode json = Json.asJson(makeJson("{'foo': 'bar'}"));

        hasJsonPath = new HasJsonPath("$.foo", is("bar"));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    @Test
    public void jsonMatchesInteger() {
        JsonNode json = Json.asJson(makeJson("{'foo': 5}"));

        hasJsonPath = new HasJsonPath("$.foo", greaterThan(4L));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    @Test
    public void jsonMatchesBoolean() {
        JsonNode json = Json.asJson(makeJson("{'foo': false}"));

        hasJsonPath = new HasJsonPath("$.foo", is(true));
        assertThat(hasJsonPath.matchesSafely(json), is(false));
    }

    @Test
    public void jsonMatchesNull() {
        JsonNode json = Json.asJson(makeJson("{'foo': null}"));

        hasJsonPath = new HasJsonPath("$.foo", is(nullValue()));
        assertThat(hasJsonPath.matchesSafely(json), is(true));
    }

    private String makeJson(String fakeJson) {
        return fakeJson.replace("'", "\"");
    }

}


