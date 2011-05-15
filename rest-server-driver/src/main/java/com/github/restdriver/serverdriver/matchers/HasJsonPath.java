package com.github.restdriver.serverdriver.matchers;

import com.jayway.jsonpath.JsonPath;
import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import java.text.ParseException;

/**
 * Matcher to enable assertions on JSON objects using JSONpath.
 */
public final class HasJsonPath extends TypeSafeMatcher<JsonNode> {

    private final String jsonPath;
    private final Matcher<?> matcher;

    /**
     * Constructor.
     *
     * @param jsonPath The JSONpath to use.
     * @param matcher The matcher to apply to the result of the JSONpath.
     */
    public HasJsonPath(String jsonPath, Matcher<?> matcher) {
        this.jsonPath = jsonPath;
        this.matcher = matcher;
    }

    @Override
    public boolean matchesSafely(JsonNode jsonNode) {

        try {
            return matcher.matches(JsonPath.read(jsonNode.toString(), jsonPath));

        } catch (ParseException e) {
            // we weren't passed valid JSON, which can only happen if Jackson produces bad JSON...
            return false;
        }

    }

    @Override
    public void describeTo(Description description) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
