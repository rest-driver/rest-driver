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

import static com.github.restdriver.clientdriver.ClientDriverRequest.Method.*;
import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

public class ClientDriverSuccessTest {
    
    @Rule
    public ClientDriverRule driver = new ClientDriverRule();
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testJettyWorking200() throws Exception {
        
        driver.addExpectation(
                onRequestTo("/blah"),
                giveResponse("OUCH!!")
                        .withStatus(200)
                        .withContentType("text/plain")
                        .withHeader("Server", "TestServer"));
        
        HttpClient client = new DefaultHttpClient();
        
        String baseUrl = driver.getBaseUrl();
        HttpGet getter = new HttpGet(baseUrl + "/blah");
        
        HttpResponse response = client.execute(getter);
        
        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response.getEntity().getContent()), is("OUCH!!"));
        assertThat(response.getHeaders("Server")[0].getValue(), is("TestServer"));
        
    }
    
    @Test
    public void testJettyWorking404() throws Exception {
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(onRequestTo("/blah2"), giveResponse("o.O").withStatus(404));
        
        HttpClient client = new DefaultHttpClient();
        HttpGet getter = new HttpGet(baseUrl + "/blah2");
        HttpResponse response = client.execute(getter);
        
        assertThat(response.getStatusLine().getStatusCode(), is(404));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("o.O"));
        
    }
    
    @Test
    public void testJettyWorking500() throws Exception {
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(onRequestTo("/blah2"), giveResponse("___").withStatus(500));
        
        HttpClient client = new DefaultHttpClient();
        HttpGet getter = new HttpGet(baseUrl + "/blah2");
        HttpResponse response = client.execute(getter);
        
        assertThat(response.getStatusLine().getStatusCode(), is(500));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("___"));
        
    }
    
    @Test
    public void testJettyWorkingTwoRequests() throws Exception {
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(onRequestTo("/blah123"), giveResponse("__2_").withStatus(200));
        driver.addExpectation(onRequestTo("/blah456"), giveResponse("__7_").withStatus(300));
        
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
        
        driver.addExpectation(
                onRequestTo("/blah").withMethod(Method.DELETE).withParam("gang", "green"),
                giveResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
                        "TestServer"));
        
        HttpClient client = new DefaultHttpClient();
        
        String baseUrl = driver.getBaseUrl();
        HttpDelete deleter = new HttpDelete(baseUrl + "/blah?gang=green");
        
        HttpResponse response = client.execute(deleter);
        
        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("OUCH!!"));
        assertThat(response.getHeaders("Server")[0].getValue(), equalTo("TestServer"));
        
    }
    
    @Test
    public void testJettyWorkingWithMethodAndParamsPattern() throws Exception {
        
        driver.addExpectation(
                onRequestTo(Pattern.compile("/[a-z]l[a-z]{2}")).withMethod(Method.DELETE).withParam("gang",
                        Pattern.compile("gre[a-z]+")),
                giveResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
                        "TestServer"));
        
        HttpClient client = new DefaultHttpClient();
        
        String baseUrl = driver.getBaseUrl();
        HttpDelete deleter = new HttpDelete(baseUrl + "/blah?gang=green");
        
        HttpResponse response = client.execute(deleter);
        
        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }
    
    @Test
    public void testJettyWorkingTwoSameRequests() throws Exception {
        
        driver.addExpectation(
                onRequestTo("/blah"),
                giveResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server", "TestServer"));
        
        driver.addExpectation(
                onRequestTo("/blah"),
                giveResponse("OUCH!!404").withStatus(404).withContentType("text/plain404").withHeader("Server", "TestServer404"));
        
        HttpClient client = new DefaultHttpClient();
        
        String baseUrl = driver.getBaseUrl();
        
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
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(
                onRequestTo("/blah2").withMethod(Method.PUT).withBody("Jack your body!", "text/plain"),
                giveResponse("___").withStatus(501));
        
        HttpClient client = new DefaultHttpClient();
        HttpPut putter = new HttpPut(baseUrl + "/blah2");
        putter.setEntity(new StringEntity("Jack your body!", "text/plain", "UTF-8"));
        HttpResponse response = client.execute(putter);
        
        assertThat(response.getStatusLine().getStatusCode(), is(501));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("___"));
        
    }
    
    @Test
    public void testJettyWorkingWithPostBodyPattern() throws Exception {
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(
                onRequestTo("/blah2").withMethod(Method.PUT).withBody(Pattern.compile("Jack [\\w\\s]+!"),
                        "text/plain"), giveResponse("___").withStatus(501));
        
        HttpClient client = new DefaultHttpClient();
        HttpPut putter = new HttpPut(baseUrl + "/blah2");
        putter.setEntity(new StringEntity("Jack your body!", "text/plain", "UTF-8"));
        HttpResponse response = client.execute(putter);
        
        assertThat(response.getStatusLine().getStatusCode(), is(501));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("___"));
    }
    
    @Test
    public void testHttpOPTIONS() throws Exception {
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(
                onRequestTo("/blah2").withMethod(Method.OPTIONS),
                giveResponse(null).withStatus(200).withHeader("Allow", "POST, OPTIONS"));
        
        HttpClient client = new DefaultHttpClient();
        HttpOptions options = new HttpOptions(baseUrl + "/blah2");
        HttpResponse response = client.execute(options);
        
        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(response.getHeaders("Allow")[0].getValue(), equalTo("POST, OPTIONS"));
    }
    
    @Test
    public void testJettyWorkingWithHeaderString() throws Exception {
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(
                onRequestTo("/header").withMethod(Method.GET),
                giveEmptyResponse().withStatus(204).withHeader("Cache-Control", "no-cache"));
        
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(baseUrl + "/header");
        HttpResponse response = client.execute(get);
        
        assertThat(response.getStatusLine().getStatusCode(), is(204));
        assertThat(response.getHeaders("Cache-Control")[0].getValue(), equalTo("no-cache"));
        
    }
    
    @Test
    public void matchingOnRequestHeader() throws Exception {
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(
                onRequestTo("/header").withMethod(Method.GET).withHeader("X-FOO", "bar"),
                giveEmptyResponse().withStatus(204).withHeader("Cache-Control", "no-cache"));
        
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(baseUrl + "/header");
        get.addHeader(new BasicHeader("X-FOO", "bar"));
        HttpResponse response = client.execute(get);
        
        assertThat(response.getStatusLine().getStatusCode(), is(204));
        assertThat(response.getHeaders("Cache-Control")[0].getValue(), equalTo("no-cache"));
        
    }
    
    @Test
    public void failedMatchingOnRequestHeader() throws Exception {
        
        thrown.expect(ClientDriverFailedExpectationException.class);
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(
                onRequestTo("/header").withMethod(Method.GET).withHeader("X-FOO", "bar"),
                giveEmptyResponse().withStatus(204).withHeader("Cache-Control", "no-cache"));
        
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(baseUrl + "/header");
        get.addHeader(new BasicHeader("X-FOO", "baz"));
        client.execute(get);
    }
    
    @Test
    public void testHttpHEADMatchesHttpGETExceptForEntity() throws Exception {
        
        String baseUrl = driver.getBaseUrl();
        String URL = baseUrl + "/blah2";
        
        driver.addExpectation(
                onRequestTo("/blah2").withMethod(Method.GET),
                giveResponse("something").withStatus(200).withHeader("Allow", "GET, HEAD"));
        driver.addExpectation(
                onRequestTo("/blah2").withMethod(Method.HEAD),
                giveResponse("something").withStatus(200).withHeader("Allow", "GET, HEAD"));
        
        HttpClient client = new DefaultHttpClient();
        
        HttpHead headRequest = new HttpHead(URL);
        HttpResponse headResponse = client.execute(headRequest);
        
        HttpGet getRequest = new HttpGet(URL);
        HttpResponse getResponse = client.execute(getRequest);
        
        assertThat(headResponse.getStatusLine().getStatusCode(), is(200));
        assertThat(headResponse.getHeaders("Allow")[0].getValue(), equalTo("GET, HEAD"));
        assertThat(headResponse.getAllHeaders().length, is(getResponse.getAllHeaders().length));
        
        String getEntityBody = EntityUtils.toString(getResponse.getEntity());
        assertThat(getEntityBody, is("something"));
        
        assertThat(headResponse.getEntity(), nullValue());
    }
    
    @Test
    public void testMatchingOnMultipleParameters() throws Exception {
        String url = driver.getBaseUrl() + "/testing?key=value1&key=value2";
        
        driver.addExpectation(
                onRequestTo("/testing").withMethod(Method.GET).withParam("key", "value1").withParam("key", "value2"),
                giveResponse("something").withStatus(200));
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet getRequest = new HttpGet(url);
        HttpResponse getResponse = client.execute(getRequest);
        
        String getEntityBody = EntityUtils.toString(getResponse.getEntity());
        assertThat(getEntityBody, is("something"));
    }
    
    @Test
    public void testHttpTRACE() throws ClientProtocolException, IOException {
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(
                onRequestTo("/blah2").withMethod(Method.TRACE),
                giveResponse(null).withStatus(200));
        
        HttpClient client = new DefaultHttpClient();
        HttpTrace trace = new HttpTrace(baseUrl + "/blah2");
        HttpResponse response = client.execute(trace);
        
        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }
    
    @Test
    public void testPostWithBodyOverTwoExpectations() throws Exception {
        // bug fix
        
        String baseUrl = driver.getBaseUrl();
        driver.addExpectation(onRequestTo("/foo"), giveResponse("___").withStatus(417));
        driver.addExpectation(onRequestTo("/blah2").withBody("<eh/>", "application/xml").withMethod(POST), giveResponse("___").withStatus(418));
        
        HttpClient postClient = new DefaultHttpClient();
        HttpPost poster = new HttpPost(baseUrl + "/blah2");
        poster.setEntity(new StringEntity("<eh/>", "application/xml", "iso-8859-1"));
        HttpResponse response = postClient.execute(poster);
        
        HttpClient getClient = new DefaultHttpClient();
        HttpGet getter = new HttpGet(baseUrl + "/foo");
        HttpResponse getResponse = getClient.execute(getter);

        assertThat(getResponse.getStatusLine().getStatusCode(), equalTo(417));
        
        assertThat(response.getStatusLine().getStatusCode(), is(418));
        assertThat(IOUtils.toString(response.getEntity().getContent()), equalTo("___"));
        
    }
    
    @Test
    public void bodyIsAllowedForGet() throws Exception {
        
        driver.addExpectation(
                onRequestTo("/foo").withBody("A BODY", "text/plain"),
                giveEmptyResponse().withStatus(418));
        
        HttpClient client = new DefaultHttpClient();
        HttpMethodWithBody get = new HttpMethodWithBody("GET", driver.getBaseUrl() + "/foo");
        get.setEntity(new StringEntity("A BODY"));
        HttpResponse response = client.execute(get);
        
        assertThat(response.getStatusLine().getStatusCode(), is(418));
        
    }
    
    @Test
    public void bodyIsAllowedForDelete() throws Exception {
        
        driver.addExpectation(
                onRequestTo("/foo").withMethod(Method.DELETE).withBody("A BODY", "text/plain"),
                giveEmptyResponse().withStatus(418));
        
        HttpClient client = new DefaultHttpClient();
        HttpMethodWithBody delete = new HttpMethodWithBody("DELETE", driver.getBaseUrl() + "/foo");
        delete.setEntity(new StringEntity("A BODY"));
        HttpResponse response = client.execute(delete);
        
        assertThat(response.getStatusLine().getStatusCode(), is(418));
        
    }
    
    private class HttpMethodWithBody extends HttpEntityEnclosingRequestBase {
        private final String method;
        
        public HttpMethodWithBody(String method, String uri) {
            super();
            this.method = method;
            setURI(URI.create(uri));
        }
        
        @Override
        public String getMethod() {
            return method;
        }
    }
}
