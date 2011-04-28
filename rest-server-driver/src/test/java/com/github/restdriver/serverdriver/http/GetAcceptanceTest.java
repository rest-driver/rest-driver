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
package com.github.restdriver.serverdriver.http;

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

public class GetAcceptanceTest extends ClientDriverUnitTest {

    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = super.getClientDriver().getBaseUrl();
    }

    @Test
    public void simpleGetRetrievesStatusAndContent() {
        getClientDriver().addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse("Content"));

        Response response = get(baseUrl);

        assertThat(response, hasStatusCode(200));
        assertThat(response.getContent(), is("Content"));
    }

    @Test
    public void getRetrievesHeaders() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("").withStatus(409).withHeader("X-foo", "barrr"));

        Response response = get(baseUrl);

        assertThat(response, hasStatusCode(409));
        assertThat(response, hasHeaderWithValue("X-foo", equalTo("barrr")));

    }

    @Test
    public void getIncludesResponseTime() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("Hello"));

        Response response = get(baseUrl);

        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }

    @Test
    public void getSendsHeaders() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("Hello"));

        // TODO: ClientDriver doesn't match on headers yet,
        // so we don't know if they are actually being sent!

        Response response = get(baseUrl, header("Accept: Nothing"));

        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }

}
