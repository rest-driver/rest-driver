package com.github.restdriver.serverdriver;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

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

    public static JsonNode asJson(Response response) {
        return asJson(response.getContent());
    }

    public static JsonNode asJson(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create JSON node", e);
        }
    }

    public static TypeSafeMatcher<JsonNode> hasJsonValue(String fieldName, Matcher<?> matcher) {
        return new HasJsonValue(fieldName, matcher);
    }

    public static TypeSafeMatcher<JsonNode> hasJsonArray(String fieldName, Matcher<?> matcher) {
        return new HasJsonArray(fieldName, matcher);
    }

    public static TypeSafeMatcher<JsonNode> containingValue(Matcher<?> matcher) {
        return new ContainingValue(matcher);
    }

    public static TypeSafeMatcher<JsonNode> withValueAt(int position, Matcher<?> matcher) {
        return new WithValueAt(position, matcher);
    }

    public static TypeSafeMatcher<JsonNode> withSize(Matcher<?> matcher) {
        return new WithSize(matcher);
    }

}
