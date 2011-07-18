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
package com.github.restdriver.clientdriver.unit;

import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.regex.Pattern;

import org.junit.Test;

public class RestClientDriverTest {

    @Test
    public void onRequestToWithStringWorks() {
        String path = "/path";
        assertThat((String) onRequestTo(path).getPath(), is(path));
    }

    @Test
    public void onRequestToWithPatternWorks() {
        Pattern path = Pattern.compile(".+");
        assertThat((Pattern) onRequestTo(path).getPath(), is(path));
    }

    @Test
    public void giveResponseWorks() {
        String content = "some wonderful content";
        assertThat(giveResponse(content).getContent(), is(content));
    }

    @Test
    public void giveEmptyResponseWorks() {
        assertThat(giveEmptyResponse().getContent(), is(""));
    }
}
