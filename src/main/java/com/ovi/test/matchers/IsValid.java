package com.ovi.test.matchers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import eu.vahlas.json.schema.impl.JSONValidator;
import eu.vahlas.json.schema.impl.JacksonSchema;

public class IsValid extends TypeSafeMatcher<JsonNode> {

	private static Map<String, JsonNode> schemas = new HashMap<String, JsonNode>();
	private final JsonNode schema;

	public IsValid(final JsonNode schema) {
		this.schema = schema;
	}

	public IsValid(final URL url) {
		this(getSchema(url));
	}

	private static synchronized JsonNode getSchema(final URL url) {
		if (!schemas.containsKey(url.toString())) {
			final ObjectMapper mapper = new ObjectMapper();

			try {
				final JsonNode schema = mapper.readTree(url.openStream());
				schemas.put(url.toString(), schema);
			} catch (final JsonProcessingException e) {
				throw new RuntimeException("JSON schema cannot be processed, content appears to be invalid: " + url.toString(), e);
			} catch (final IOException e) {
				throw new RuntimeException("Unable to read JSON schema from URL: " + url.toString(), e);
			}
		}

		return schemas.get(url.toString());
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("JsonNode valid against schema ");
		description.appendValue(schema);
	}

	@Override
	public boolean matchesSafely(final JsonNode jsonNode) {
		final List<String> validationMessages = new JacksonSchema(schema).validate(jsonNode, JSONValidator.AT_ROOT);

		for (final String message : validationMessages) {

			// No way with hamcrest 1.1 for matcher to report more information
			// about mismatch, so System.out is a last resort. When moving to
			// hamcrest 1.2 we can extend TypeSafeDiagnosingMatcher instead

			System.out.println("JSON VALIDATION ERROR: " + message);
		}

		return validationMessages.isEmpty();
	}

}
