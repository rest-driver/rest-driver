package com.github.restdriver.serverdriver.matchers;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher to check that a JSON array has a particular value at the specified index.
 */
public final class WithValueAt extends TypeSafeMatcher<JsonNode> {

    private final int position;
    private final Matcher<?> matcher;

    /**
     * Create a new instance of this matcher.
     * 
     * @param position The position in the array at which the value is to be evaluated
     * @param matcher The matcher to use to evaluate the value
     */
    public WithValueAt(int position, Matcher<?> matcher) {
        this.position = position;
        this.matcher = matcher;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("A JSON array with value at " + position + " which matches: ");
        matcher.describeTo(description);
    }

    @Override
    public boolean matchesSafely(JsonNode node) {

        if (!node.isArray()) {
            return false;
        }

        Iterator<JsonNode> nodeIterator = node.getElements();
        int nodeCount = 0;

        while (nodeIterator.hasNext()) {

            JsonNode currentNode = nodeIterator.next();

            if (nodeCount == position) {
                return matcher.matches(currentNode.getTextValue());
            }

            nodeCount++;

        }

        return false;
    }
}
