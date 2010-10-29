package com.ovi.test.matchers;

import static com.ovi.test.AcceptanceTestHelper.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

public class IsValidTest {

    @Test
    public void testValidObject() throws IOException {

        final JsonNode jsonObject = jsonToNode(getResource("/json/valid-person.json"));

        final JsonNode schema = jsonToNode(getResource("/json/person-schema.json"));

        assertThat(jsonObject, is(valid(schema)));
    }

    @Test
    public void testInvalidObject() throws IOException {

        final JsonNode jsonObject = jsonToNode(getResource("/json/invalid-person.json"));

        final JsonNode schema = jsonToNode(getResource("/json/person-schema.json"));

        assertThat(jsonObject, not(valid(schema)));
    }

    private String getResource(String path) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(path));
    }

}
