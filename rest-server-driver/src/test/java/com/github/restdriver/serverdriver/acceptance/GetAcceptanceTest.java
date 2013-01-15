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

import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static com.github.restdriver.serverdriver.Matchers.*;
import static com.github.restdriver.serverdriver.RestServerDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.serverdriver.http.Url;
import com.github.restdriver.serverdriver.http.response.Response;

public class GetAcceptanceTest {
    
    @Rule
    public ClientDriverRule driver = new ClientDriverRule();
    
    private String baseUrl;
    
    @Before
    public void getServerDetails() {
        baseUrl = driver.getBaseUrl();
    }
    
    @Test
    public void simpleGetRetrievesStatusAndContent() {
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse("Content"));
        
        Response response = get(baseUrl);
        
        assertThat(response, hasStatusCode(200));
        assertThat(response.getContent(), is("Content"));
    }
    
    @Test
    public void getRetrievesHeaders() {
        driver.addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("").withStatus(409).withHeader("X-foo", "barrr"));
        
        Response response = get(baseUrl);
        
        assertThat(response, hasStatusCode(409));
        assertThat(response, hasHeaderWithValue("X-foo", equalTo("barrr")));
        
    }
    
    @Test
    public void getIncludesResponseTime() {
        driver.addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("Hello"));
        
        Response response = get(baseUrl);
        
        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }
    
    @Test
    public void getSendsHeaders() {
        driver.addExpectation(
                new ClientDriverRequest("/").withHeader("Accept", "Nothing"),
                new ClientDriverResponse("Hello"));
        
        Response response = get(baseUrl, header("Accept: Nothing"));
        
        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }
    
    @Test
    public void getDoesntFollowRedirects() {
        driver.addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("")
                        .withStatus(303)
                        .withHeader("Location", "http://foobar"));
        
        Response response = get(baseUrl);
        
        assertThat(response, hasStatusCode(303));
        assertThat(response, hasHeader("Location", "http://foobar"));
    }
    
    @Test
    public void getAllowsUrlObjects() {
        driver.addExpectation(
                onRequestTo("/").withParam("a", "b"),
                giveResponse("yooo").withStatus(404));
        
        Url url = url(baseUrl).withParam("a", "b");
        
        Response response = get(url);
        
        assertThat(response, hasStatusCode(404));
    }
    
    @Test
    public void getAllowsBodyContent() {
        driver.addExpectation(
                onRequestTo("/").withBody("BODIEZ!", "text/plain"),
                giveEmptyResponse().withStatus(418));
        
        Response response = get(baseUrl, body("BODIEZ!", "text/plain"));
        assertThat(response, hasStatusCode(418));
        
    }
    
}
