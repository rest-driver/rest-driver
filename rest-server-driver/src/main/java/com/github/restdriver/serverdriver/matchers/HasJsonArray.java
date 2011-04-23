package com.github.restdriver.serverdriver.matchers;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher to verify that a JsonNode has an array with the specified field name.
 */
public final class HasJsonArray extends TypeSafeMatcher<JsonNode> {

    private final String fieldName;
    private final Matcher<?> arrayMatcher;

    /**
     * Create an instance of this matcher.
     * 
     * @param fieldName The field name to check for
     * @param arrayMatcher The matcher to be used to check the array found at the given field name 
     */
    public HasJsonArray(String fieldName, Matcher<?> arrayMatcher) {
        this.fieldName = fieldName;
        this.arrayMatcher = arrayMatcher;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("JsonNode with '" + fieldName + "' matching: ");
        arrayMatcher.describeTo(description);
    }

    @Override
    public boolean matchesSafely(JsonNode jsonNode) {

        JsonNode node = jsonNode.get(fieldName);

        if (node == null) {
            return false;
        }

        if (node.isArray()) {

            return arrayMatcher.matches(node);

        } else {
            return false;

        }

    }

}
