package com.github.restdriver.serverdriver.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Matcher to check that an HttpResponse has the specified body.
 */
public final class HasResponseBody extends TypeSafeMatcher<Response> {

    private final Matcher<String> responseMatcher;

    public HasResponseBody(Matcher<String> responseMatcher) {
        this.responseMatcher = responseMatcher;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("HttpMethod with response body matching:");
        responseMatcher.describeTo(description);
    }

    @Override
    public boolean matchesSafely(Response actualResponse) {

        String actualContent = actualResponse.getContent();

        return responseMatcher.matches(actualContent);
    }

}
