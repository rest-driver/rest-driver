package com.github.restdriver.matchers;

import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static com.github.restdriver.matchers.HasXPath.hasStringXPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HasXPathTest {

    @Test
    public void xmlMatchesStringWithValue() {
        TypeSafeMatcher<String> xpathMatcher = hasStringXPath("//elementName", "value");

        assertThat(xpathMatcher.matches("<elementName>value</elementName>"), is(true));
    }

    @Test
    public void xmlMatchesString() {
        TypeSafeMatcher<String> xpathMatcher = hasStringXPath("//elementName");

        assertThat(xpathMatcher.matches("<elementName>something else</elementName>"), is(true));
    }

    @Test
    public void xmlDoesNotMatch() {
        TypeSafeMatcher<String> xpathMatcher = hasStringXPath("/node");

        assertThat(xpathMatcher.matches("<cat><node></node></cat>"), is(false));
    }
}
