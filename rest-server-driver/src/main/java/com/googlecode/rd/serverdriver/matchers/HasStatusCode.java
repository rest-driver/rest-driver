package com.googlecode.rd.serverdriver.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.googlecode.rd.serverdriver.http.response.Response;

public class HasStatusCode extends TypeSafeMatcher<Response> {

	private final Matcher<Integer> statusCodeMatcher;

	public HasStatusCode(final Matcher<Integer> statusCodeMatcher) {
		this.statusCodeMatcher = statusCodeMatcher;
	}

	@Override
	protected final boolean matchesSafely(final Response item) {
		return statusCodeMatcher.matches(item.getStatusCode());
	}

	@Override
	public final void describeTo(final Description description) {
		description.appendText("Response with status code matching: ");
		statusCodeMatcher.describeTo(description);
	}

	@Override
	protected final void describeMismatchSafely(final Response item, final Description mismatchDescription) {
		mismatchDescription.appendText("Response has status code " + item.getStatusCode() + " and body " + item.getContent());
	}

}
