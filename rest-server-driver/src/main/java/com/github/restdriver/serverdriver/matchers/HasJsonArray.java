package com.github.restdriver.serverdriver.matchers;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class HasJsonArray extends TypeSafeMatcher<JsonNode> {

	private final String jsonFieldName;
	private final Matcher<?> responseMatcher;

	public HasJsonArray(String jsonNode, Matcher<?> responseMatcher) {
		this.jsonFieldName = jsonNode;
		this.responseMatcher = responseMatcher;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("JsonNode with '" + jsonFieldName + "' matching: ");
		responseMatcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(JsonNode jsonNode) {

		 JsonNode node = jsonNode.get(jsonFieldName);

		if (node == null) {
			return false;
		}

		if (node.isArray()) {

			return responseMatcher.matches(node);

		} else {
			return false;

		}

	}

}
