package com.github.restdriver.serverdriver.matchers;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Matcher to enable assertion on HTTP header values.
 * 
 * TODO: Is this class necessary we can do:
 * <p/>
 * assertThat(response.getHeaders(), hasItem(new Header("header", "value")))
 */
public final class HasHeaderWithValue extends TypeSafeMatcher<Response> {

    private final String name;
    private final Matcher<String> valueMatcher;

    public HasHeaderWithValue(String name, Matcher<String> valueMatcher) {
        this.name = name;
        this.valueMatcher = valueMatcher;
    }

    @Override
    protected boolean matchesSafely(Response response) {

        for (Header header : response.getHeaders()) {
            if (!StringUtils.equals(header.getName(), name)) {
                continue;
            }

            return valueMatcher.matches(header.getValue());
        }

        return false;

    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Response with header named '" + name + "' and value matching: ");
        valueMatcher.describeTo(description);
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
