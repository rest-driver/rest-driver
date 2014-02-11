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

import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.clientdriver.HttpRealRequest;
import com.github.restdriver.clientdriver.MatchedRequestHandler;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

public class ClientDriverRuleTest {
    
    @Rule
    public ClientDriverRule driver = new ClientDriverRule();
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void letsTrySomethingThatFails() throws Exception {
        
        // We use ExpectedException to catch the exception we (hopefully) get because the expectations weren't met
        thrown.expect(ClientDriverFailedExpectationException.class);
        
        driver.addExpectation(onRequestTo("/blah"), giveResponse("OUCH!!", "text/plain").withStatus(200));
        driver.addExpectation(onRequestTo("/blah"), giveResponse("OUCH!!", "text/plain").withStatus(404));
        
        HttpClient client = new DefaultHttpClient();
        
        HttpPost post = new HttpPost(driver.getBaseUrl() + "/blah?gang=groon");
        
        client.execute(post);
        
    }
    
    @Test
    public void letsTrySomethingThatWorks() throws Exception {
        
        driver.addExpectation(onRequestTo("/blah"), giveResponse("", null).withStatus(404));
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet get = new HttpGet(driver.getBaseUrl() + "/blah");
        
        client.execute(get);
        
    }
    
    @Test
    public void letsTrySomethingThatShouldCallbackOnMatch() throws Exception {
        
        final boolean[] matched = new boolean[1];
        
        driver.addExpectation(
                onRequestTo("/path").withMethod(ClientDriverRequest.Method.POST),
                giveResponse("", null))
                .anyTimes()
                .whenMatched(new MatchedRequestHandler() {
                    
                    @Override
                    public void onMatch(HttpRealRequest matchedRequest) {
                        assertThat(new String(matchedRequest.getBodyContent()), is("body"));
                        matched[0] = true;
                    }
                    
                });
        
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(driver.getBaseUrl() + "/path");
        post.setEntity(new StringEntity("body"));
        client.execute(post);
        
        assertThat(matched[0], is(true));
    }
    
    @Test
    public void responseExpectationTimeoutIsPropagatedToClientDriverResponse() {
        
        // Given
        final ClientDriverRule driver = new ClientDriverRule()
                .expectResponsesWithin(5, TimeUnit.DAYS);
        final ClientDriverResponse response = giveResponse("", null);
        
        // When
        driver.addExpectation(
                onRequestTo("/path"),
                response);
        
        // Then
        assertThat(response.hasNotExpired(), is(true));
    }
    
    @Test
    public void responseExpectationTimeoutDefaultsToImmediately() {
        
        // Given
        final ClientDriverRule driver = new ClientDriverRule();
        final ClientDriverResponse response = giveResponse("", null);
        
        // When
        driver.addExpectation(
                onRequestTo("/path"),
                response);
        
        // Then
        assertThat(response.canExpire(), is(false));
    }
    
    @Test
    public void responseTimeoutOverridesClientDriverRuleExpectationTimeout() throws InterruptedException {
        
        // Given
        final ClientDriverRule driver = new ClientDriverRule()
                .expectResponsesWithin(5, TimeUnit.MINUTES);
        final ClientDriverResponse response = giveResponse("", null);
        
        // When
        driver.addExpectation(
                onRequestTo("/path"),
                response.within(1, TimeUnit.MILLISECONDS));
        
        Thread.sleep(5);
        
        // Then
        assertThat(response.hasNotExpired(), is(false));
    }
}
