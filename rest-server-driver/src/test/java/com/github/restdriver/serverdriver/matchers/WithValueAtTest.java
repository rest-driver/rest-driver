package com.github.restdriver.serverdriver.matchers;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.TextNode;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

public class WithValueAtTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private WithValueAt matcher;

    @Before
    public void before() {
        matcher = new WithValueAt(1, is("bar"));
    }

    @Test
    public void matcherDescribesItselfCorrectly() {
        Description description = new StringDescription();
        matcher.describeTo(description);

        assertThat(description.toString(), is("A JSON array with value at 1 which matches: is \"bar\""));
    }

    @Test
    public void matcherShouldFailWhenAskedToMatchNonArrayNode() {
        assertThat(matcher.matches(new TextNode("bar")), is(false));
    }

    @Test
    public void matcherShouldFailWhenGivenEmptyArrayNode() {
        assertThat(matcher.matches(array()), is(false));
    }

    @Test
    public void matcherShouldFailWhenRequiredElementDoesntMatch() {
        assertThat(matcher.matches(array("notbar")), is(false));
    }

    @Test
    public void matcherShouldPassWhenRequiredElementMatches() {
        assertThat(matcher.matches(array("foo", "bar")), is(true));
    }

    private ArrayNode array(String... items) {
        ArrayNode array = MAPPER.createArrayNode();
        for (String item : items) {
            array.add(item);
        }
        return array;
    }
}
