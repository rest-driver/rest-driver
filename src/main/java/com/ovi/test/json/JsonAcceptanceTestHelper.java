package com.ovi.test.json;

import java.io.IOException;
import java.net.URL;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.ovi.test.http.Response;
import com.ovi.test.matchers.ContainingValue;
import com.ovi.test.matchers.HasJsonArray;
import com.ovi.test.matchers.HasJsonValue;
import com.ovi.test.matchers.IsValid;
import com.ovi.test.matchers.WithSize;
import com.ovi.test.matchers.WithValueAt;

public final class JsonAcceptanceTestHelper {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static JsonNode asJson(final Response response) {
		return asJson(response.getContent());
	}

	public static JsonNode asJson(final String json) {
		try {
			return MAPPER.readTree(json);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to create JSON node", e);
		}
	}

	public static TypeSafeMatcher<JsonNode> hasJsonValue(final String fieldName, final Matcher<?> matcher) {
		return new HasJsonValue(fieldName, matcher);
	}

	public static TypeSafeMatcher<JsonNode> hasJsonArray(final String fieldName, final Matcher<?> matcher) {
		return new HasJsonArray(fieldName, matcher);
	}

	public static TypeSafeMatcher<JsonNode> containingValue(final Matcher<?> matcher) {
		return new ContainingValue(matcher);
	}

	public static TypeSafeMatcher<JsonNode> withValueAt(final int position, final Matcher<?> matcher) {
		return new WithValueAt(position, matcher);
	}

	public static TypeSafeMatcher<JsonNode> withSize(final Matcher<?> matcher) {
		return new WithSize(matcher);
	}

	public static TypeSafeMatcher<JsonNode> valid(final JsonNode schema) {
		return new IsValid(schema);
	}

	public static TypeSafeMatcher<JsonNode> valid(final URL url) {
		return new IsValid(url);
	}

}
