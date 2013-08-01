/**
 * Copyright © 2010-2011 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.restdriver.matchers;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class HasXPathTest {

    @Test
    public void xmlMatchesStringWithValue() {
        TypeSafeMatcher<String> xPathMatcher = HasXPath.hasXPath("//elementName", is("value"));
        assertThat(xPathMatcher.matches("<elementName>value</elementName>"), is(true));
    }

    @Test
    public void xmlMatchesString() {
        TypeSafeMatcher<String> xPathMatcher = HasXPath.hasXPath("//elementName");
        assertThat(xPathMatcher.matches("<elementName>something else</elementName>"), is(true));
    }

    @Test
    public void xmlMatchesWithNestedMatcher() {
        TypeSafeMatcher<String> xPathMatcher = HasXPath.hasXPath("//elementName", containsString("la"));
        assertThat(xPathMatcher.matches("<elementName>blah</elementName>"), is(true));
    }

    @Test
    public void xmlDoesNotMatch() {
        TypeSafeMatcher<String> xPathMatcher = HasXPath.hasXPath("/node");
        assertThat(xPathMatcher.matches("<cat><node></node></cat>"), is(false));
    }
    
}
