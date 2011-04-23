package com.github.restdriver.serverdriver.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Matcher to check whether an HTTP response has a particular status code. 
 */
public final class HasStatusCode extends TypeSafeMatcher<Response> {

    private final Matcher<Integer> statusCodeMatcher;

    /**
     * Creates an instance of this matcher.
     * 
     * @param statusCodeMatcher The matcher to be used to evaluate the status code of a response
     */
    public HasStatusCode(Matcher<Integer> statusCodeMatcher) {
        this.statusCodeMatcher = statusCodeMatcher;
    }

    @Override
    protected boolean matchesSafely(Response item) {
        return statusCodeMatcher.matches(item.getStatusCode());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Response with status code matching: ");
        statusCodeMatcher.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(Response item, Description mismatchDescription) {
        mismatchDescription.appendText("Response has status code " + item.getStatusCode() + " and body " + item.getContent());
    }

}
