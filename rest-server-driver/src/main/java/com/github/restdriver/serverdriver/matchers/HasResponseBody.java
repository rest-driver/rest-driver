package com.github.restdriver.serverdriver.matchers;

import com.github.restdriver.serverdriver.http.response.Response;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

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
