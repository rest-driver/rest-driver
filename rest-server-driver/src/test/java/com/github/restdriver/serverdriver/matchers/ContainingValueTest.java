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

public class ContainingValueTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ContainingValue matcher;

    @Before
    public void before() {
        matcher = new ContainingValue(containsString("joy"));
    }

    @Test
    public void matcherShouldDescribesItselfCorrectly() {
        Description description = new StringDescription();
        matcher.describeTo(description);
        assertThat(description.toString(), is("A JSON array containing: a string containing \"joy\""));
    }

    @Test
    public void matcherShouldFailWhenAskedToMatchNonArrayNode() {
        assertThat(matcher.matches(new TextNode("foo")), is(false));
    }

    @Test
    public void matcherShouldFailWhenGivenEmptyArrayNode() {
        assertThat(matcher.matches(array()), is(false));
    }

    @Test
    public void matcherShouldFailWhenMatcherDoesntMatch() {
        assertThat(matcher.matches(array("foobar")), is(false));
    }

    @Test
    public void matcherShouldPassWhenMatcherMatches() {
        assertThat(matcher.matches(array("foobar", "enjoyment")), is(true));
    }

    private ArrayNode array(String... items) {
        ArrayNode array = MAPPER.createArrayNode();
        for (String item : items) {
            array.add(item);
        }
        return array;
    }

}
