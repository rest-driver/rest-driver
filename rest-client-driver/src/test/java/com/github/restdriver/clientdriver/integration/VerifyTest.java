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
package com.github.restdriver.clientdriver.integration;

import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriverRule;

public class VerifyTest {


    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testVerifyWithNormalConditions() throws Exception {

        driver.addExpectation(
                onRequestTo("/blah"),
                giveResponse("OUCH!!", "text/plain")
                        .withStatus(200)
                        .withHeader("Server", "TestServer"));

        HttpClient client = new DefaultHttpClient();

        String baseUrl = driver.getBaseUrl();
        HttpGet getter = new HttpGet(baseUrl + "/blah");

        HttpResponse response = client.execute(getter);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response.getEntity().getContent()), is("OUCH!!"));
        assertThat(response.getHeaders("Server")[0].getValue(), is("TestServer"));

        driver.verify(onRequestTo("/blah"), 1);
    }

    @Test
    public void testVerifyWithTwoCalls() throws Exception {

        driver.addExpectation(
                onRequestTo("/blah"),
                giveResponse("OUCH!!", "text/plain")
                        .withStatus(200)
                        .withHeader("Server", "TestServer")).anyTimes();

        HttpClient client = new DefaultHttpClient();
        HttpClient client1 = new DefaultHttpClient();

        String baseUrl = driver.getBaseUrl();
        HttpGet getter = new HttpGet(baseUrl + "/blah");

        HttpResponse response = client.execute(getter);
        HttpResponse response1 = client1.execute(getter);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response.getEntity().getContent()), is("OUCH!!"));
        assertThat(response.getHeaders("Server")[0].getValue(), is("TestServer"));

        assertThat(response1.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response1.getEntity().getContent()), is("OUCH!!"));
        assertThat(response1.getHeaders("Server")[0].getValue(), is("TestServer"));

        driver.verify(onRequestTo("/blah"), 2);
    }

    @Test
    public void testVerifyWithZeroCalls() throws Exception {

        driver.addExpectation(
                onRequestTo("/blah"),
                giveResponse("OUCH!!", "text/plain")
                        .withStatus(200)
                        .withHeader("Server", "TestServer")).anyTimes();

        driver.verify(onRequestTo("/blah"), 0);
    }

}
