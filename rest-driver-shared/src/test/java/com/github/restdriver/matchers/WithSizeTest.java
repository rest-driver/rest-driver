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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.TextNode;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

import com.github.restdriver.matchers.WithSize;

public class WithSizeTest {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    private WithSize matcher;
    
    @Before
    public void before() {
        matcher = new WithSize(lessThan(2));
    }
    
    @Test
    public void matcherShouldDescribeItselfCorrectly() {
        Description description = new StringDescription();
        matcher.describeTo(description);
        
        assertThat(description.toString(), is("A JSON array with size: a value less than <2>"));
    }
    
    @Test
    public void matcherShouldFailWhenAskedToMatchNonArrayNode() {
        assertThat(matcher.matches(new TextNode("something")), is(false));
    }
    
    @Test
    public void matcherShouldFailWhenMatcherFails() {
        assertThat(matcher.matches(array("foo", "bar")), is(false));
    }
    
    @Test
    public void matcherShouldPassWhenMatcherPasses() {
        assertThat(matcher.matches(array("foo")), is(true));
    }
    
    private ArrayNode array(String... items) {
        ArrayNode array = MAPPER.createArrayNode();
        for (String item : items) {
            array.add(item);
        }
        return array;
    }
}
