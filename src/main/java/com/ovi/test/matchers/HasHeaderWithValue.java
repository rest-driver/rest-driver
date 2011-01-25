package com.ovi.test.matchers;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.ovi.test.http.Header;
import com.ovi.test.http.Response;

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
		final Header[] headers = response.getHeaders();

		if (headers.length == 0) {
			mismatchDescription.appendText("Response has no headers");
		} else {
			mismatchDescription.appendText("Response has headers [" + StringUtils.join(response.getHeaders(), ",") + "]");
		}
	}
}
