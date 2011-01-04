package com.ovi.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.ovi.test.http.Response;

public class HasStatusCode extends TypeSafeMatcher<Response> {

	private final int statusCode;

	public HasStatusCode(final int statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	protected final boolean matchesSafely(final Response item) {
		return statusCode == item.getStatusCode();
	}

	@Override
	public final void describeTo(final Description description) {
		description.appendText("Response with status code " + statusCode);
	}

	@Override
	protected final void describeMismatchSafely(final Response item, final Description mismatchDescription) {
		mismatchDescription.appendText("Response has status code " + item.getStatusCode() + " and body " + item.getContent());
	}

}
