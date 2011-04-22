package com.github.restdriver.serverdriver.matchers;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Matcher to assert on the headers in an HTTP response.
 * 
 * TODO: Is this class necessary we can do:
 * 
 * assertThat(response.getHeaders(), hasItem(new Header("header", "value")))
 */
public final class HasHeader extends TypeSafeMatcher<Response> {

    private final String name;

    public HasHeader(String name) {
        this.name = name;
    }

    @Override
    protected boolean matchesSafely(Response response) {

        for (Header header : response.getHeaders()) {
            if (StringUtils.equals(header.getName(), name)) {
                return true;
            }
        }

        return false;

    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Response with header named '" + name + "'");
    }

    @Override
    protected void describeMismatchSafely(Response response, Description mismatchDescription) {
        List<Header> headers = response.getHeaders();

        if (headers.isEmpty()) {
            mismatchDescription.appendText("Response has no headers");
        } else {
            mismatchDescription.appendText("Response has headers [" + StringUtils.join(response.getHeaders(), ",") + "]");
        }
    }

}
