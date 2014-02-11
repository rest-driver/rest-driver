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

import java.text.ParseException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.exception.RuntimeJsonTypeMismatchException;
import com.github.restdriver.serverdriver.http.response.Response;

public class JsonPathAcceptanceTest {
    
    @Rule
    public ClientDriverRule driver = new ClientDriverRule();
    
    private String baseUrl;
    
    @Before
    public void getServerDetails() {
        baseUrl = driver.getBaseUrl();
    }
    
    @Test
    public void jsonPathCanBeRunOverJsonResponse() {
        String jsonContent = makeJson(" { 'thing' : 'valuoid' } ");
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), hasJsonPath("$.thing", equalTo("valuoid")));
    }
    
    @Test
    public void matchingNumbers() {
        String jsonContent = makeJson(" { 'thing' : 5 } ");
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), hasJsonPath("$.thing", is(5)));
    }
    
    @Test
    public void matchingNumbersAsLong() {
        String jsonContent = makeJson(" { 'thing' : 5 } ");
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), hasJsonPath("$.thing", is(5L)));
    }
    
    @Test
    public void correctHandlingOfDouble_IntMismatch() {
        String jsonContent = makeJson(" { 'thing' : 5.00 } ");
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), not(hasJsonPath("$.thing", is(5)))); // it's 5.0, not 5 dammit!
    }
    
    @Test(expected = RuntimeJsonTypeMismatchException.class)
    public void matchingIntWhenNumberOverflows() {
        String jsonContent = makeJson(" { 'thing' : 4294967294 } ");
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), hasJsonPath("$.thing", greaterThan(5)));
    }
    
    @Test(expected = RuntimeJsonTypeMismatchException.class)
    public void matchingDoubleWhenNumberOverflows() {
        String jsonContent = makeJson(" { 'thing' : 4294967294 } ");
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), hasJsonPath("$.thing", greaterThan(5.1)));
    }
    
    @Test
    public void matchingLongWhenNumberOverflowsIsOK() {
        String jsonContent = makeJson(" { 'thing' : 4294967294 } ");
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), hasJsonPath("$.thing", greaterThan(5L)));
    }
    
    @Test
    public void moreComplexJsonPathCanBeRunOverJsonResponse() throws ParseException {
        String jsonContent = makeJson(" { 'thing' : { 'sub' : { 'subsub' : 'valutron' } } } ");
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), hasJsonPath("$..subsub", hasItem(equalTo("valutron"))));
    }
    
    @Test
    public void jsonPathWithConditional() {
        String jsonContent = makeJson(
                
                " { 'things' : " +
                        "[ { 'a': 'one', 'c' : 100 } , " +
                        "  { 'a': 'two', 'c' : 150 } ] } ");
        
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), hasJsonPath("$.things[?(@.c > 125)].a", hasItem(equalTo("two"))));
    }
    
    @Test
    public void jsonPathWithoutMatcher() {
        String jsonContent = makeJson("{'this':'thing','that':3}");
        
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent, "application/json"));
        Response response = get(baseUrl);
        
        assertThat(response.asJson(), hasJsonPath("$.that"));
    }
    
    private String makeJson(String fakeJson) {
        return fakeJson.replace("'", "\"");
    }
}
