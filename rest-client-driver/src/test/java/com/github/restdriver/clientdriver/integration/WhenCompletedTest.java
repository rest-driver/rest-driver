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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverCompletedListener;
import com.github.restdriver.clientdriver.ClientDriverRule;

public class WhenCompletedTest {
    
    @Rule
    public ClientDriverRule clientDriver = new ClientDriverRule();
    
    private boolean done;
    
    @Before
    public void before() {
        done = false;
    }
    
    /**
     * This test might seem a touch weird so is worthy of comment:
     * 
     * It sets up a thread that fires after 500 milliseconds and adds an expectation to receive that request within
     * 1 second. Once the request has been sent the 'done' boolean is set to true. We add a completion listener that
     * asserts that done is true.
     * 
     * If we were to write this normally (where the assertion is simply at the end of the method) it would fail.
     */
    @Test
    public void assertionWaitsUntilClientDriverIsFinished() throws Exception {
        
        clientDriver.addExpectation(
                onRequestTo("/hello"),
                giveEmptyResponse().within(1, TimeUnit.SECONDS));
        
        clientDriver.whenCompleted(new ClientDriverCompletedListener() {
            @Override
            public void hasCompleted() {
                assertThat(done, is(true));
            }
        });
        
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                schnoozeFor(500);
                hitThat(clientDriver.getBaseUrl() + "/hello");
                done = true;
            }
        });
        thread.setDaemon(true);
        thread.start();
        
    }
    
    private static void schnoozeFor(long period) {
        try {
            Thread.sleep(period);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void hitThat(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            client.execute(get);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
