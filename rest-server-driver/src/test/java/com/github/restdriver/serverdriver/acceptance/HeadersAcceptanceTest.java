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
package com.github.restdriver.serverdriver.acceptance;

import static com.github.restdriver.serverdriver.Matchers.*;
import static com.github.restdriver.serverdriver.RestServerDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.example.ClientDriverUnitTest;
import com.github.restdriver.serverdriver.http.response.Response;

public class HeadersAcceptanceTest extends ClientDriverUnitTest {

    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = super.getClientDriver().getBaseUrl();
    }

    @Test
    public void getRetrievesHeaders() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("").withHeader("X-foo", "barrr"));

        Response response = get(baseUrl);

        // multiple ways to assert on a header
        assertThat(response, hasHeaderWithValue("X-foo", equalTo("barrr")));
        assertThat(response, hasHeader("X-foo", equalTo("barrr")));
        assertThat(response, hasHeader("X-foo", "barrr"));
        assertThat(response, hasHeader("X-foo: barrr"));
        assertThat(response, hasHeader(header("X-foo", "barrr")));
        assertThat(response, hasHeader(header("X-foo: barrr")));

        // header *names* are case insensitive
        assertThat(response, hasHeader(header("X-FOO: barrr")));
        assertThat(response, hasHeader("X-FOO: barrr"));
        assertThat(response, hasHeader("X-FOO"));

        // but values are case-sensitive
        assertThat(response, not(hasHeader(header("X-foo: BARRR"))));
        assertThat(response, not(hasHeader("X-foo: BARRR")));

    }

}
