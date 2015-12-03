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

import java.net.URI;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

public class ClientDriverFailTest {
    
    private ClientDriver clientDriver;
    
    @Test
    public void testUnexpectedCall() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        // No expectations defined
        
        HttpClient client = new DefaultHttpClient();
        HttpGet getter = new HttpGet(clientDriver.getBaseUrl() + "/blah?foo=bar");
        
        client.execute(getter);
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), containsString("1 unexpected request(s):"));
            assertThat(bre.getMessage(), containsString("GET /blah; PARAMS: [foo=[bar]];"));
        }
        
    }
    
    @Test
    public void testUnexpectedMultipleUnexpectedCalls() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        // No expectations defined
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet getter = new HttpGet(clientDriver.getBaseUrl() + "/blah?foo=bar");
        HttpResponse getResponse = client.execute(getter);
        EntityUtils.consume(getResponse.getEntity());
        
        HttpPost poster = new HttpPost(clientDriver.getBaseUrl() + "/baz/qux");
        HttpResponse postResponse = client.execute(poster);
        EntityUtils.consume(postResponse.getEntity());
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), containsString("2 unexpected request(s):"));
            assertThat(bre.getMessage(), containsString("GET /blah; PARAMS: [foo=[bar]];"));
            assertThat(bre.getMessage(), containsString("POST /baz/qux;"));
        }
        
    }
    
    @Test
    public void testUnmatchedExpectation() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        clientDriver.addExpectation(onRequestTo("/blah"), giveResponse("OUCH!!", "text/plain").withStatus(200));
        clientDriver.addExpectation(onRequestTo("/blah"), giveResponse("OUCH!!", "text/plain").withStatus(404));
        
        // no requests made
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), containsString("2 unmatched expectation(s)"));
            assertThat(bre.getMessage(), containsString("expected: 1, actual: 0"));
            assertThat(bre.getMessage(), containsString("GET \"/blah\""));
        }
        
    }
    
    @Test
    public void testJettyWorkingWithMethodButIncorrectParams() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        clientDriver.addExpectation(onRequestTo("/blah").withMethod(Method.POST).withParam("gang", "green"),
                giveResponse("OUCH!!", "text/plain").withStatus(200).withHeader("Server", "TestServer"));
        
        HttpClient client = new DefaultHttpClient();
        
        String baseUrl = clientDriver.getBaseUrl();
        HttpPost poster = new HttpPost(baseUrl + "/blah?gang=groon");
        
        client.execute(poster);
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), containsString("1 unexpected request(s):"));
            assertThat(bre.getMessage(), containsString("POST /blah; PARAMS: [gang=[groon]];"));
        }
        
    }
    
    @Test
    public void testJettyWorkingWithMethodButIncorrectParamsPattern() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        clientDriver.addExpectation(onRequestTo(Pattern.compile("/b[a-z]{3}")).withMethod(Method.POST).withParam(
                "gang", Pattern.compile("r")), giveResponse("OUCH!!", "text/plain").withStatus(200).withHeader("Server", "TestServer"));
        
        HttpClient client = new DefaultHttpClient();
        
        String baseUrl = clientDriver.getBaseUrl();
        HttpPost poster = new HttpPost(baseUrl + "/blah?gang=goon");
        
        client.execute(poster);
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), containsString("1 unexpected request(s):"));
            assertThat(bre.getMessage(), containsString("POST /blah; PARAMS: [gang=[goon]]"));
        }
        
    }
    
    @Test
    public void testJettyWorkingWithIncorrectHeaderString() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        clientDriver.addExpectation(onRequestTo("/test").withHeader("Content-Length", "1234"),
                giveEmptyResponse().withStatus(204).withHeader("Content-Type", "abcd"));
        
        HttpClient client = new DefaultHttpClient();
        
        String baseUrl = clientDriver.getBaseUrl();
        
        HttpGet getter = new HttpGet(baseUrl + "/test");
        
        client.execute(getter);
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), containsString("1 unexpected request(s):"));
            assertThat(bre.getMessage(), containsString("GET /test;"));
        }
    }
    
    @Test
    public void testJettyWorkingWithIncorrectHeaderPattern() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        clientDriver.addExpectation(onRequestTo("/test").withHeader("Content-Length", Pattern.compile("\\d+")),
                giveEmptyResponse().withStatus(204).withHeader("Content-Type", "abcd"));
        
        HttpClient client = new DefaultHttpClient();
        
        String baseUrl = clientDriver.getBaseUrl();
        
        HttpGet getter = new HttpGet(baseUrl + "/test");
        
        client.execute(getter);
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), containsString("1 unexpected request(s):"));
            assertThat(bre.getMessage(), containsString("GET /test"));
        }
    }
    
    @Test
    public void testFailedMatchOnMultipleParameters() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        String url = clientDriver.getBaseUrl() + "/testing?key=value3&key=value2";
        
        clientDriver.addExpectation(
                onRequestTo("/testing").withMethod(Method.GET).withParam("key", "value1").withParam("key", "value2"),
                giveResponse("something", "text/plain").withStatus(200));
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet getRequest = new HttpGet(url);
        client.execute(getRequest);
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), containsString("1 unexpected request(s):"));
            assertThat(bre.getMessage(), anyOf(containsString("GET /testing; PARAMS: [key=[value3, value2]]; "),
                    containsString("GET /testing; PARAMS: [key=[value2, value3]]; ")));
        }
        
    }
    
    @Test
    public void getWithBodyFailsIfMatcherFails() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        clientDriver.addExpectation(
                onRequestTo("/foo").withMethod(Method.GET).withBody("BODY", "text/plain"),
                giveEmptyResponse().withStatus(418));
        
        HttpClient client = new DefaultHttpClient();
        HttpMethodWithBody get = new HttpMethodWithBody("GET", clientDriver.getBaseUrl() + "/foo");
        client.execute(get);
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException e) {
            assertThat(e.getMessage(), containsString("1 unexpected request(s):"));
            assertThat(e.getMessage(), containsString("GET /foo;"));
        }
    }
    
    @Test
    public void deleteWithBodyFailsIfMatcherFails() throws Exception {
        clientDriver = new ClientDriverFactory().createClientDriver();
        
        clientDriver.addExpectation(
                onRequestTo("/foo").withMethod(Method.DELETE).withBody("BODY", "text/plain"),
                giveEmptyResponse().withStatus(418));
        
        HttpClient client = new DefaultHttpClient();
        HttpMethodWithBody delete = new HttpMethodWithBody("DELETE", clientDriver.getBaseUrl() + "/foo");
        client.execute(delete);
        
        try {
            clientDriver.shutdown();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException e) {
            assertThat(e.getMessage(), containsString("1 unexpected request(s):"));
            assertThat(e.getMessage(), containsString("DELETE /foo;"));
        }
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
