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

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

public class HeadAcceptanceTest {
    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = driver.getBaseUrl();
    }

    @Test
    public void simpleHeadRetrievesStatus() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.HEAD),
                new ClientDriverResponse());

        Response response = head(baseUrl);

        assertThat(response, hasStatusCode(204));
    }

    @Test
    public void headIgnoresEntity() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.HEAD),
                new ClientDriverResponse("some content"));

        Response response = headOf(baseUrl);

        assertThat(response.getContent(), nullValue());
    }

    @Test
    public void getOnSameResourceAsHeadRequestRetrievesSameHeadersButWithAnEntity() {

        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.HEAD),
                new ClientDriverResponse("Content"));
        Response headResponse = doHeadOf(baseUrl);

        assertThat(headResponse, hasStatusCode(200));

        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.GET),
                new ClientDriverResponse("Content"));
        Response getResponse = get(baseUrl);

        assertThat(getResponse, hasStatusCode(200));
        assertThat(getResponse.getContent(), not(nullValue()));

        List<Header> getHeaders = getResponse.getHeaders();
        List<Header> headHeaders = headResponse.getHeaders();

        for (Header getHeader : getHeaders) {
            assertThat("The GET request had a header that the HEAD headers did not.", headHeaders, hasItem(getHeader));
        }

        for (Header headHeader : headHeaders) {
            assertThat("The HEAD request had a header that the GET headers did not.", getHeaders, hasItem(headHeader));
        }
    }

    @Test
    public void headRetrievesHeaders() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.HEAD),
                new ClientDriverResponse("").withStatus(409).withHeader("X-foo", "barrr"));

        Response response = head(baseUrl);

        assertThat(response, hasStatusCode(409));
        assertThat(response, hasHeaderWithValue("X-foo", equalTo("barrr")));

    }

    @Test
    public void headIncludesResponseTime() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.HEAD),
                new ClientDriverResponse());

        Response response = head(baseUrl);

        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }

    @Test
    public void headSendsHeaders() {
        driver.addExpectation(
                new ClientDriverRequest("/")
                        .withMethod(Method.HEAD)
                        .withHeader("Accept", "Nothing"),
                new ClientDriverResponse("Hello"));

        Response response = head(baseUrl, header("Accept: Nothing"));

        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }

    @Test
    public void headDoesntFollowRedirects() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.HEAD),
                new ClientDriverResponse("")
                        .withStatus(303)
                        .withHeader("Location", "http://foobar"));

        Response response = head(baseUrl);

        assertThat(response, hasStatusCode(303));
        assertThat(response, hasHeader("Location", "http://foobar"));
    }

}
