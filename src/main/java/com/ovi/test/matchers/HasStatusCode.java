package com.ovi.test.matchers;

import org.apache.commons.httpclient.HttpMethod;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class HasStatusCode extends TypeSafeMatcher<HttpMethod> {

	private final int expectedStatusCode;

	public HasStatusCode(int expectedStatusCode) {
		this.expectedStatusCode = expectedStatusCode;
	}

	public void describeTo(Description description) {
		description.appendText("HttpMethod with status code of " + expectedStatusCode);
	}

	@Override
	public boolean matchesSafely(HttpMethod actualMethod) {
		return actualMethod.getStatusCode() == expectedStatusCode;
	}

}
