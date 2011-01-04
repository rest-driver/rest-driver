package com.ovi.test.matchers;

import static com.ovi.test.json.JsonAcceptanceTestHelper.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

public class IsValidTest {

	@Test
	public void testValidObject() throws IOException {

		final JsonNode jsonObject = asJson(getResource("/json/valid-person.json"));

		final JsonNode schema = asJson(getResource("/json/person-schema.json"));

		assertThat(jsonObject, is(valid(schema)));
	}

	@Test
	public void testInvalidObject() throws IOException {

		final JsonNode jsonObject = asJson(getResource("/json/invalid-person.json"));

		final JsonNode schema = asJson(getResource("/json/person-schema.json"));

		assertThat(jsonObject, not(valid(schema)));
	}

	private String getResource(final String path) throws IOException {
		return IOUtils.toString(this.getClass().getResourceAsStream(path));
	}

}
