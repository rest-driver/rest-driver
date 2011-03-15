package com.googlecode.rd.serverdriver.matchers;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class WithValueAt extends TypeSafeMatcher<JsonNode> {

	private final int position;
	private final Matcher<?> matcher;

	public WithValueAt(final int position, final Matcher<?> matcher) {
		this.position = position;
		this.matcher = matcher;
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("A JSON array with value at " + position + " which matches: ");
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

			final JsonNode currentNode = nodeIterator.next();

			if (nodeCount == position) {
				return matcher.matches(currentNode.getTextValue());
			}

			nodeCount++;

		}

		return false;
	}
}
