package com.github.restdriver.serverdriver.matchers;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

public class HasJsonArrayTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private HasJsonArray matcher;

    @Before
    public void before() {
        matcher = new HasJsonArray("array", new WithSize(is(2)));
    }

    @Test
    public void matcherShouldDescribeItselfCorrectly() {
        Description description = new StringDescription();
        matcher.describeTo(description);

        assertThat(description.toString(), is("JsonNode with 'array' matching: A JSON array with size: is <2>"));
    }

    @Test
    public void matcherShouldFailWhenNodeDoesntContainFieldWithGivenName() {
        assertThat(matcher.matches(object("foo", new TextNode("bar"))), is(false));
    }

    @Test
    public void matcherShouldFailWhenAskedToMatchNonArrayNode() {
        assertThat(matcher.matches(object("array", new TextNode("foo"))), is(false));
    }

    @Test
    public void matcherShouldFailWhenArrayDoesNotMatch() {
        assertThat(matcher.matches(object("array", array("foobar"))), is(false));
    }

    @Test
    public void matcherShouldPassWhenArrayMatches() {
        assertThat(matcher.matches(object("array", array("foo", "bar"))), is(true));
    }

    private ObjectNode object(String name, JsonNode value) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put(name, value);
        return node;
    }

    private ArrayNode array(String... items) {
        ArrayNode node = MAPPER.createArrayNode();
        for (String item : items) {
            node.add(item);
        }
        return node;
    }

}
