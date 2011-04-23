package com.github.restdriver.serverdriver.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Matcher to check that an HttpResponse has the specified body.
 */
public final class HasResponseBody extends TypeSafeMatcher<Response> {

    private final Matcher<String> bodyMatcher;

    /**
     * Creates a new instance of this matcher.
     * 
     * @param bodyMatcher The matcher to be used to evaluate the body of the response
     */
    public HasResponseBody(Matcher<String> bodyMatcher) {
        this.bodyMatcher = bodyMatcher;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("HttpMethod with response body matching:");
        bodyMatcher.describeTo(description);
    }

    @Override
    public boolean matchesSafely(Response actualResponse) {

        String actualContent = actualResponse.getContent();

        return bodyMatcher.matches(actualContent);
    }

}
