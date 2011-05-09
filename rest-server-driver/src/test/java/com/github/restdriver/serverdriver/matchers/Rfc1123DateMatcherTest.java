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
package com.github.restdriver.serverdriver.matchers;

import com.github.restdriver.serverdriver.http.Header;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * User: mjg
 * Date: 09/05/11
 * Time: 20:02
 */
public class Rfc1123DateMatcherTest {

    private final Header compliantDate = new Header("Date", "Mon, 09 May 2011 18:49:18 GMT");
    private final Header unCompliantDate = new Header("Date", "Junk, 09 May 2011 18:49:18 GMT");

    @Test
    public void testCorrectMatches() {
        assertThat(new Rfc1123DateMatcher().matches(compliantDate), is(true));
    }

    @Test
    public void testIncorrectDoesntMatch() {
        assertThat(new Rfc1123DateMatcher().matches(unCompliantDate), is(false));
    }

}
