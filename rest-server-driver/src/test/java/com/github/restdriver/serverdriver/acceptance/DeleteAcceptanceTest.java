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
import org.junit.Rule;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.serverdriver.http.response.Response;

public class DeleteAcceptanceTest {
    
    @Rule
    public ClientDriverRule driver = new ClientDriverRule();
    
    private String baseUrl;
    
    @Before
    public void getServerDetails() {
        baseUrl = driver.getBaseUrl();
    }
    
    @Test
    public void simpleDeleteRetrievesStatusAndContent() {
        
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(ClientDriverRequest.Method.DELETE),
                new ClientDriverResponse("Content", "text/plain"));
        
        Response response = delete(baseUrl);
        
        assertThat(response, hasStatusCode(200));
        assertThat(response.getContent(), is("Content"));
    }
    
    @Test
    public void deleteSendsHeaders() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(ClientDriverRequest.Method.DELETE).withHeader("Accept", "Nothing"),
                new ClientDriverResponse("Hello", "text/plain"));
        
        Response response = delete(baseUrl, header("Accept", "Nothing"));
        assertThat(response.getContent(), is("Hello"));
    }
    
    @Test
    public void deleteAllowsBodyContent() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(ClientDriverRequest.Method.DELETE).withBody("A BODY?", "text/plain"),
                new ClientDriverResponse("Hurrah", "text/plain").withStatus(418));
        
        Response response = delete(baseUrl, body("A BODY?", "text/plain"));
        assertThat(response.getStatusCode(), is(418));
    }
    
}
