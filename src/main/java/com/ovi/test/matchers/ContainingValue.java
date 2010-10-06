package com.ovi.test.matchers;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ContainingValue extends TypeSafeMatcher<JsonNode> {

	private final Matcher<?> matcher;

	public ContainingValue(Matcher<?> matcher) {
		this.matcher = matcher;
	}

	public void describeTo(Description description) {
		description.appendText("A JSON array containing: ");
		matcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(JsonNode node) {

		if (!node.isArray()) {
			return false;
		}

		Iterator<JsonNode> nodeIterator = node.getElements();

		while (nodeIterator.hasNext()) {

			String value = nodeIterator.next().getTextValue();

			if (matcher.matches(value)) {
				return true;
			}
		}

		return false;
	}

}
