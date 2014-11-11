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

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.serverdriver.http.response.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.restdriver.serverdriver.Matchers.hasStatusCode;
import static com.github.restdriver.serverdriver.RestServerDriver.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AnyMethodAcceptanceTest {

    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = driver.getBaseUrl();
    }

    @Test
    public void anyMethodEmptyBody() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.custom("PROPFIND")),
                new ClientDriverResponse("Content", "text/plain"));

        Response response = method("PROPFIND", baseUrl);

        assertThat(response, hasStatusCode(200));
        assertThat(response.getContent(), is("Content"));
    }

    @Test
    public void anyMethodWithTextPlainBody() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.custom("baseline-control")).withBody("Your body", "text/plain"),
                new ClientDriverResponse("Back at you", "text/plain").withStatus(202));

        Response response = method("BASELINE-CONTROL", baseUrl, body("Your body", "text/plain"));

        assertThat(response, hasStatusCode(202));
        assertThat(response.getContent(), is("Back at you"));
    }

    @Test
    public void anyMethodWithApplicationXmlBody() {
        driver.addExpectation(
                new ClientDriverRequest("/")
                        .withMethod(Method.custom("SEARCH"))
                        .withBody("<yo/>", "application/xml"),
                new ClientDriverResponse("Back at you", "text/plain").withStatus(202));

        Response response = method("search", baseUrl, body("<yo/>", "application/xml"));

        assertThat(response, hasStatusCode(202));
        assertThat(response.getContent(), is("Back at you"));
    }

    @Test
    public void anyMethodWithApplicationJsonBodyAndHeaders() {
        driver.addExpectation(
                new ClientDriverRequest("/jsons")
                        .withMethod(Method.custom("patch"))
                        .withBody("<yo/>", "application/xml")
                        .withHeader("Accept", "Nothing"),
                new ClientDriverResponse("Back at you", "text/plain").withStatus(202));

        Response response = method("patch", baseUrl + "/jsons", body("<yo/>", "application/xml"), header("Accept", "Nothing"));

        assertThat(response, hasStatusCode(202));
        assertThat(response.getContent(), is("Back at you"));
    }

    @Test
    public void anyMethodWithDuplicateBodyUsesLastOne() {
        driver.addExpectation(
                new ClientDriverRequest("/xml")
                        .withMethod(Method.custom("PATCH"))
                        .withBody("<yo/>", "application/xml"),
                new ClientDriverResponse("Back at you", "text/plain").withStatus(202));

        Response response = method("patch", baseUrl + "/xml", body("{}", "application/json"), body("<yo/>", "application/xml"));

        assertThat(response, hasStatusCode(202));
        assertThat(response.getContent(), is("Back at you"));
    }

    @Test
    public void anyMethodWithByteArrayBody() {
        driver.addExpectation(
                new ClientDriverRequest("/bytes")
                        .withMethod(Method.custom("patch"))
                        .withBody("some bytes", "application/pdf"),
                new ClientDriverResponse("The response", "text/plain").withStatus(418));

        Response response = method("patch", baseUrl + "/bytes", body("some bytes".getBytes(), "application/pdf"));

        assertThat(response, hasStatusCode(418));
        assertThat(response.getContent(), is("The response"));
    }
}


