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
package com.github.restdriver.serverdriver;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.exception.RuntimeMappingException;
import com.github.restdriver.serverdriver.http.response.Response;
import com.github.restdriver.serverdriver.matchers.ContainingValue;
import com.github.restdriver.serverdriver.matchers.HasJsonArray;
import com.github.restdriver.serverdriver.matchers.HasJsonValue;
import com.github.restdriver.serverdriver.matchers.WithSize;
import com.github.restdriver.serverdriver.matchers.WithValueAt;

/**
 * Class supplying static methods to help with JSON representations.
 */
public final class Json {

    private Json() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Converts the content of the response to a JSON node.
     * 
     * @param response
     *            The response whose content is to be converted
     * @return The converted JSON node
     */
    public static JsonNode asJson(Response response) {
        return asJson(response.getContent());
    }

    /**
     * Converts the given string to a JSON node.
     * 
     * @param json
     *            The string which is to be converted
     * @return The converted JSON node
     */
    public static JsonNode asJson(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeMappingException("Failed to create JSON node", e);
        }
    }

    /**
     * Creates a new instance of HasJsonValue.
     * 
     * @param fieldName
     *            The name of the field in the JSON node which will be evaluated
     * @param matcher
     *            The matcher to use for evaluation
     * @return The new matcher
     */
    public static TypeSafeMatcher<JsonNode> hasJsonValue(String fieldName, Matcher<?> matcher) {
        return new HasJsonValue(fieldName, matcher);
    }

    /**
     * Creates a new instance of HasJsonArray.
     * 
     * @param fieldName
     *            The name of the field in the JSON node which will be evaluated
     * @param matcher
     *            The matcher to use for evaluation
     * @return The new matcher
     */
    public static TypeSafeMatcher<JsonNode> hasJsonArray(String fieldName, Matcher<?> matcher) {
        return new HasJsonArray(fieldName, matcher);
    }

    /**
     * Creates a new instance of ContainingValue.
     * 
     * @param matcher
     *            The matcher to use for evaluation
     * @return The new matcher
     */
    public static TypeSafeMatcher<JsonNode> containingValue(Matcher<?> matcher) {
        return new ContainingValue(matcher);
    }

    /**
     * Creates a new instance of WithValueAt.
     * 
     * @param position
     *            The position of the value to be evaluated
     * @param matcher
     *            The matcher to use for evaluation
     * @return The new matcher
     */
    public static TypeSafeMatcher<JsonNode> withValueAt(int position, Matcher<?> matcher) {
        return new WithValueAt(position, matcher);
    }

    /**
     * Creates a new instance of WithSize.
     * 
     * @param matcher
     *            The matcher to use for evaluation
     * @return The new matcher
     */
    public static TypeSafeMatcher<JsonNode> withSize(Matcher<Integer> matcher) {
        return new WithSize(matcher);
    }

}
