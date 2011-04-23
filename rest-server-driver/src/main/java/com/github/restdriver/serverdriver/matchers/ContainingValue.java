package com.github.restdriver.serverdriver.matchers;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher which checks that a JSON array contains the specified value.
 */
public final class ContainingValue extends TypeSafeMatcher<JsonNode> {

    private final Matcher<?> matcher;

    /**
     * Create a new instance which uses the given matcher against all values in a JSON array.
     * 
     * @param matcher The matcher to be used for evaluation
     */
    public ContainingValue(Matcher<?> matcher) {
        this.matcher = matcher;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("A JSON array containing: ");
        matcher.describeTo(description);
    }

    @Override
    public boolean matchesSafely(JsonNode node) {

        if (!node.isArray()) {
            return false;
        }

        Iterator<JsonNode> nodeIterator = node.getElements();

        while (nodeIterator.hasNext()) {

            String value = nodeIterator.next().getTextValue();

            if (matcher.matches(value)) {
                return true;
            }
        }

        return false;
    }

}
