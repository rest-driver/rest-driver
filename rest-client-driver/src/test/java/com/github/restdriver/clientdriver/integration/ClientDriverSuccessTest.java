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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.example.ClientDriverUnitTest;

public class ClientDriverSuccessTest extends ClientDriverUnitTest {

    @Test
    public void testJettyWorking200() throws Exception {

        getClientDriver().addExpectation(
                new ClientDriverRequest("/blah"),
                new ClientDriverResponse("OUCH!!")
                        .withStatus(200)
                        .withContentType("text/plain")
                        .withHeader("Server", "TestServer"));

        HttpClient client = new DefaultHttpClient();

        String baseUrl = getClientDriver().getBaseUrl();
        HttpGet getter = new HttpGet(baseUrl + "/blah");

        HttpResponse response = client.execute(getter);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response.getEntity().getContent()), is("OUCH!!"));
        assertThat(response.getHeaders("Server")[0].getValue(), is("TestServer"));

    }

    @Test
    public void testJettyWorking404() throws Exception {

        String baseUrl = getClientDriver().getBaseUrl();
        getClientDriver().addExpectation(new ClientDriverRequest("/blah2"), new ClientDriverResponse("o.O").withStatus(404));

        HttpClient client = new DefaultHttpClient();
        HttpGet getter = new HttpGet(baseUrl + "/blah2");
        HttpResponse response = client.execute(getter);

        assertThat(response.getStatusLine().getStatusCode(), is(404));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("o.O"));

    }

    @Test
    public void testJettyWorking500() throws Exception {

        String baseUrl = getClientDriver().getBaseUrl();
        getClientDriver().addExpectation(new ClientDriverRequest("/blah2"), new ClientDriverResponse("___").withStatus(500));

        HttpClient client = new DefaultHttpClient();
        HttpGet getter = new HttpGet(baseUrl + "/blah2");
        HttpResponse response = client.execute(getter);

        assertThat(response.getStatusLine().getStatusCode(), is(500));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("___"));

    }

    @Test
    public void testJettyWorkingTwoRequests() throws Exception {

        String baseUrl = getClientDriver().getBaseUrl();
        getClientDriver().addExpectation(new ClientDriverRequest("/blah123"), new ClientDriverResponse("__2_").withStatus(200));
        getClientDriver().addExpectation(new ClientDriverRequest("/blah456"), new ClientDriverResponse("__7_").withStatus(300));

        HttpClient client = new DefaultHttpClient();

        HttpGet getter1 = new HttpGet(baseUrl + "/blah123");
        HttpResponse response1 = client.execute(getter1);
        assertThat(response1.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response1.getEntity().getContent()), equalTo("__2_"));

        HttpGet getter2 = new HttpGet(baseUrl + "/blah456");
        HttpResponse response2 = client.execute(getter2);
        assertThat(response2.getStatusLine().getStatusCode(), is(300));
        assertThat(IOUtils.toString(response2.getEntity().getContent()), equalTo("__7_"));
    }

    @Test
    public void testJettyWorkingWithMethodAndParams() throws Exception {

        getClientDriver().addExpectation(
                new ClientDriverRequest("/blah").withMethod(Method.DELETE).withParam("gang", "green"),
                new ClientDriverResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
                        "TestServer"));

        HttpClient client = new DefaultHttpClient();

        String baseUrl = getClientDriver().getBaseUrl();
        HttpDelete deleter = new HttpDelete(baseUrl + "/blah?gang=green");

        HttpResponse response = client.execute(deleter);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("OUCH!!"));
        assertThat(response.getHeaders("Server")[0].getValue(), equalTo("TestServer"));

    }

    @Test
    public void testJettyWorkingWithMethodAndParamsPattern() throws Exception {

        getClientDriver().addExpectation(
                new ClientDriverRequest(Pattern.compile("/[a-z]l[a-z]{2}")).withMethod(Method.DELETE).withParam("gang",
                        Pattern.compile("gre[a-z]+")),
                new ClientDriverResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
                        "TestServer"));

        HttpClient client = new DefaultHttpClient();

        String baseUrl = getClientDriver().getBaseUrl();
        HttpDelete deleter = new HttpDelete(baseUrl + "/blah?gang=green");

        HttpResponse response = client.execute(deleter);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void testJettyWorkingTwoSameRequests() throws Exception {

        getClientDriver().addExpectation(
                new ClientDriverRequest("/blah"),
                new ClientDriverResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server", "TestServer"));

        getClientDriver().addExpectation(
                new ClientDriverRequest("/blah"),
                new ClientDriverResponse("OUCH!!404").withStatus(404).withContentType("text/plain404").withHeader("Server", "TestServer404"));

        HttpClient client = new DefaultHttpClient();

        String baseUrl = getClientDriver().getBaseUrl();

        HttpGet getter = new HttpGet(baseUrl + "/blah");
        HttpResponse response = client.execute(getter);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("OUCH!!"));
        assertThat(response.getHeaders("Server")[0].getValue(), equalTo("TestServer"));

        HttpGet getter2 = new HttpGet(baseUrl + "/blah");
        HttpResponse response2 = client.execute(getter2);

        assertThat(response2.getStatusLine().getStatusCode(), is(404));
        assertThat(IOUtils.toString(response2.getEntity().getContent()), equalTo("OUCH!!404"));
        assertThat(response2.getHeaders("Server")[0].getValue(), equalTo("TestServer404"));

    }

    @Test
    public void testJettyWorkingWithPostBody() throws Exception {

        String baseUrl = getClientDriver().getBaseUrl();
        getClientDriver().addExpectation(
                new ClientDriverRequest("/blah2").withMethod(Method.PUT).withBody("Jack your body!", "text/plain"),
                new ClientDriverResponse("___").withStatus(501));

        HttpClient client = new DefaultHttpClient();
        HttpPut putter = new HttpPut(baseUrl + "/blah2");
        putter.setEntity(new StringEntity("Jack your body!", "text/plain", "UTF-8"));
        HttpResponse response = client.execute(putter);

        assertThat(response.getStatusLine().getStatusCode(), is(501));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("___"));

    }

    @Test
    public void testJettyWorkingWithPostBodyPattern() throws Exception {

        String baseUrl = getClientDriver().getBaseUrl();
        getClientDriver().addExpectation(
                new ClientDriverRequest("/blah2").withMethod(Method.PUT).withBody(Pattern.compile("Jack [\\w\\s]+!"),
                        "text/plain"), new ClientDriverResponse("___").withStatus(501));

        HttpClient client = new DefaultHttpClient();
        HttpPut putter = new HttpPut(baseUrl + "/blah2");
        putter.setEntity(new StringEntity("Jack your body!", "text/plain", "UTF-8"));
        HttpResponse response = client.execute(putter);

        assertThat(response.getStatusLine().getStatusCode(), is(501));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("___"));
    }

    @Test
    public void testHttpOPTIONS() throws Exception {

        String baseUrl = getClientDriver().getBaseUrl();
        getClientDriver().addExpectation(
                new ClientDriverRequest("/blah2").withMethod(Method.OPTIONS),
                new ClientDriverResponse(null).withStatus(200).withHeader("Allow", "POST, OPTIONS"));

        HttpClient client = new DefaultHttpClient();
        HttpOptions options = new HttpOptions(baseUrl + "/blah2");
        HttpResponse response = client.execute(options);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(response.getHeaders("Allow")[0].getValue(), equalTo("POST, OPTIONS"));
    }

    @Test
    public void testJettyWorkingWithHeaderString() throws Exception {

        String baseUrl = getClientDriver().getBaseUrl();
        getClientDriver().addExpectation(
                new ClientDriverRequest("/header").withMethod(Method.GET),
                new ClientDriverResponse().withStatus(204).withHeader("Cache-Control", "no-cache"));

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(baseUrl + "/header");
        HttpResponse response = client.execute(get);

        assertThat(response.getStatusLine().getStatusCode(), is(204));
        assertThat(response.getHeaders("Cache-Control")[0].getValue(), equalTo("no-cache"));

    }

}
