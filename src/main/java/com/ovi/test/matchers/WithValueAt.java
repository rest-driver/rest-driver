package com.ovi.test.matchers;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class WithValueAt extends TypeSafeMatcher<JsonNode> {

	private final int position;
	private final Matcher<?> matcher;

	public WithValueAt(int position, Matcher<?> matcher) {
		this.position = position;
		this.matcher = matcher;
	}

	public void describeTo(Description description) {
		description.appendText("A JSON array with value at " + position + " which matches: ");
		matcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(JsonNode node) {

		if (!node.isArray()) {
			return false;
		}

		Iterator<JsonNode> nodeIterator = node.getElements();
		int nodeCount = 0;

		while (nodeIterator.hasNext()) {

			JsonNode currentNode = nodeIterator.next();

			if (nodeCount == position) {
				return matcher.matches(currentNode.getTextValue());
			}

			nodeCount++;

		}

		return false;
	}
}
