package com.github.restdriver.serverdriver.matchers;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher to check that a given key has a particular value in a JsonNode.
 */
public final class HasJsonValue extends TypeSafeMatcher<JsonNode> {

    private final String fieldName;
    private final Matcher<?> valueMatcher;

    /**
     * Creates an instance of this matcher.
     * 
     * @param fieldName The field name against which the matcher will be evaluated
     * @param valueMatcher The matcher to be used to evaluate the value found at the given field name
     */
    public HasJsonValue(String fieldName, Matcher<?> valueMatcher) {
        this.fieldName = fieldName;
        this.valueMatcher = valueMatcher;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("JsonNode with '" + fieldName + "' matching: ");
        valueMatcher.describeTo(description);
    }

    @Override
    public boolean matchesSafely(JsonNode jsonNode) {

        JsonNode node = jsonNode.get(fieldName);

        if (node == null) {
            return false;
        }

        if (node.isInt()) {
            return valueMatcher.matches(node.getIntValue());

        } else if (node.isTextual()) {
            return valueMatcher.matches(node.getTextValue());

        } else {
            return false;

        }

    }

}
