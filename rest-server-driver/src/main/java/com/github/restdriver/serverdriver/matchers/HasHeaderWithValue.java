package com.github.restdriver.serverdriver.matchers;

import java.util.List;

import com.github.restdriver.serverdriver.http.response.Response;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.types.Header;

/**
 * TODO: Is this class necessary we can do:
 * 
 * assertThat(response.getHeaders(), hasItem(new Header("header", "value")))
 */
public class HasHeaderWithValue extends TypeSafeMatcher<Response> {

	private final String name;
	private final Matcher<String> valueMatcher;

	public HasHeaderWithValue(final String name, final Matcher<String> valueMatcher) {
		this.name = name;
		this.valueMatcher = valueMatcher;
	}

	@Override
	protected final boolean matchesSafely(final Response response) {

		for (final Header header : response.getHeaders()) {
			if (!StringUtils.equals(header.getName(), name)) {
				continue;
			}

			return valueMatcher.matches(header.getValue());
		}

		return false;

	}

	@Override
	public final void describeTo(final Description description) {
		description.appendText("Response with header named '" + name + "' and value matching: ");
		valueMatcher.describeTo(description);
	}

	@Override
	protected final void describeMismatchSafely(final Response response, final Description mismatchDescription) {
		final List<Header> headers = response.getHeaders();

		if (headers.isEmpty()) {
			mismatchDescription.appendText("Response has no headers");
		} else {
			mismatchDescription.appendText("Response has headers [" + StringUtils.join(response.getHeaders(), ",") + "]");
		}
	}
}
