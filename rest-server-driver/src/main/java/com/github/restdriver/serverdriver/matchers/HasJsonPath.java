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

import com.jayway.jsonpath.JsonPath;
import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.text.ParseException;

/**
 * Matcher to enable assertions on JSON objects using JSONpath.
 */
public final class HasJsonPath extends TypeSafeMatcher<JsonNode> {

    private final String jsonPath;
    private final Matcher<?> matcher;

    private JsonNode givenNode;

    /**
     * Constructor.
     *
     * @param jsonPath The JSONpath to use.
     * @param matcher  The matcher to apply to the result of the JSONpath.
     */
    public HasJsonPath(String jsonPath, Matcher<?> matcher) {
        this.jsonPath = jsonPath;
        this.matcher = matcher;
    }

    @Override
    public boolean matchesSafely(JsonNode jsonNode) {

        givenNode = jsonNode;

        try {
            return matcher.matches(JsonPath.read(jsonNode.toString(), jsonPath));

        } catch (ParseException e) {
            // we weren't passed valid JSON, which can only happen if Jackson produces bad JSON...
            return false;
        }

    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a JSON object matching JSONpath \"" + jsonPath + "\" with ");
        matcher.describeTo(description);

        String result;

        try {
            result = JsonPath.read(givenNode.toString(), jsonPath);

        } catch (ParseException pe) {
            result = "ParseException";
        }

        description.appendText(", got " + result);
    }
}
