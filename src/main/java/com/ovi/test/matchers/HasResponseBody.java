package com.ovi.test.matchers;

import java.io.IOException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class HasResponseBody extends TypeSafeMatcher<HttpMethod> {

	private final Matcher<String> responseMatcher;

	public HasResponseBody(Matcher<String> responseMatcher) {
		this.responseMatcher = responseMatcher;
	}

	public void describeTo(Description description) {
		description.appendText("HttpMethod with response body matching:");
		responseMatcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(HttpMethod actualMethod) {

		String actualResponse;
		try {
			actualResponse = IOUtils.toString(actualMethod.getResponseBodyAsStream());

		} catch (IOException e) {
			throw new RuntimeException("Failed to read response body for matching.", e);
		}

		return responseMatcher.matches(actualResponse);
	}

}
