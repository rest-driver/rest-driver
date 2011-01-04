package com.ovi.test.matchers;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class WithSize extends TypeSafeMatcher<JsonNode> {

	private final Matcher<?> matcher;

	public WithSize(final Matcher<?> matcher) {
		this.matcher = matcher;
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("A JSON array with size: ");
		matcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(final JsonNode node) {

		if (!node.isArray()) {
			return false;
		}

		final Iterator<JsonNode> nodeIterator = node.getElements();
		int nodeCount = 0;

		while (nodeIterator.hasNext()) {
			nodeIterator.next();
			nodeCount++;
		}

		return matcher.matches(nodeCount);
	}

}
