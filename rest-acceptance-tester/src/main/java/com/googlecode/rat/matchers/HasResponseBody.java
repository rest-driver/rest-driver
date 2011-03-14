package com.googlecode.rat.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.googlecode.rat.http.response.Response;

public class HasResponseBody extends TypeSafeMatcher<Response> {

	private final Matcher<String> responseMatcher;

	public HasResponseBody(final Matcher<String> responseMatcher) {
		this.responseMatcher = responseMatcher;
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("HttpMethod with response body matching:");
		responseMatcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(final Response actualResponse) {

		final String actualContent = actualResponse.getContent();

		return responseMatcher.matches(actualContent);
	}

}
