package com.ovi.test.matchers;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ContainingValue extends TypeSafeMatcher<JsonNode> {

	private final Matcher<?> matcher;

	public ContainingValue(final Matcher<?> matcher) {
		this.matcher = matcher;
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("A JSON array containing: ");
		matcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(final JsonNode node) {

		if (!node.isArray()) {
			return false;
		}

		final Iterator<JsonNode> nodeIterator = node.getElements();

		while (nodeIterator.hasNext()) {

			final String value = nodeIterator.next().getTextValue();

			if (matcher.matches(value)) {
				return true;
			}
		}

		return false;
	}

}
