package com.ovi.test.matchers;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class HasJsonValue extends TypeSafeMatcher<JsonNode> {

	private final String jsonFieldName;
	private final Matcher<?> responseMatcher;

	public HasJsonValue(String jsonNode, Matcher<?> responseMatcher) {
		this.jsonFieldName = jsonNode;
		this.responseMatcher = responseMatcher;
	}

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

		if (node.isInt()) {
			return responseMatcher.matches(node.getIntValue());

		} else if (node.isTextual()) {
			return responseMatcher.matches(node.getTextValue());

		} else {
			return false;

		}

	}

}
