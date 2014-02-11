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
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

public class VerifyWithinTest {
    
    private ClientDriver clientDriver;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Before
    public void before() {
        clientDriver = new ClientDriverFactory().createClientDriver();
    }
    
    @Test
    public void defaultBehaviourIsToVerifyImmediately() throws Exception {
        
        thrown.expect(ClientDriverFailedExpectationException.class);
        
        clientDriver.addExpectation(
                onRequestTo("/foo"),
                giveEmptyResponse());
        
        // Don't actually do anything
        clientDriver.verify();
        
    }
    
    @Test
    public void expectationWhichMatchesAnyTimesDoesNotWait() throws Exception {
        
        clientDriver.addExpectation(
                onRequestTo("/foo"),
                giveEmptyResponse().within(10, TimeUnit.SECONDS)).anyTimes();
        
        long start = System.currentTimeMillis();
        
        clientDriver.verify();
        
        long end = System.currentTimeMillis();
        
        assertThat(end - start, is(lessThan(5000L)));
        
    }
    
    @Test
    public void verifyingSingleRequestWithinATimePeriodWorks() throws Exception {
        
        clientDriver.addExpectation(
                onRequestTo("/foo"),
                giveEmptyResponse().within(2000, TimeUnit.MILLISECONDS));
        
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(500, TimeUnit.MILLISECONDS);
                hitThat(clientDriver.getBaseUrl() + "/foo");
            }
        });
        thread.setDaemon(true);
        
        thread.start();
        
        clientDriver.verify();
        
    }
    
    @Test
    public void singleRequestThatIsNotMatchedInTimeFailsToVerify() throws Exception {
        
        thrown.expect(ClientDriverFailedExpectationException.class);
        
        clientDriver.addExpectation(
                onRequestTo("/foo"),
                giveEmptyResponse().within(200, TimeUnit.MILLISECONDS));
        
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(500, TimeUnit.MILLISECONDS);
                hitThat(clientDriver.getBaseUrl() + "/foo");
            }
        });
        thread.setDaemon(true);
        
        thread.start();
        
        clientDriver.verify();
        
    }
    
    @Test
    public void multipleRequestsWhichAreMatchedInTimeWork() throws Exception {
        
        clientDriver.addExpectation(
                onRequestTo("/foo1"),
                giveEmptyResponse().within(5, TimeUnit.SECONDS));
        
        clientDriver.addExpectation(
                onRequestTo("/foo2"),
                giveEmptyResponse().within(5, TimeUnit.SECONDS));
        
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(500, TimeUnit.MILLISECONDS);
                hitThat(clientDriver.getBaseUrl() + "/foo1");
            }
        });
        thread1.setDaemon(true);
        
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(500, TimeUnit.MILLISECONDS);
                hitThat(clientDriver.getBaseUrl() + "/foo2");
            }
        });
        thread2.setDaemon(true);
        
        thread1.start();
        thread2.start();
        
        clientDriver.verify();
        
    }
    
    @Test
    public void multipleRequestsOneOfWhichWasNotMatchedInTimeFailsToVerify() throws Exception {
        
        thrown.expect(ClientDriverFailedExpectationException.class);
        
        clientDriver.addExpectation(
                onRequestTo("/foo1"),
                giveEmptyResponse().within(200, TimeUnit.MILLISECONDS));
        
        clientDriver.addExpectation(
                onRequestTo("/foo2"),
                giveEmptyResponse().within(1, TimeUnit.SECONDS));
        
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(500, TimeUnit.MILLISECONDS);
                hitThat(clientDriver.getBaseUrl() + "/foo1");
            }
        });
        thread1.setDaemon(true);
        
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(500, TimeUnit.MILLISECONDS);
                hitThat(clientDriver.getBaseUrl() + "/foo2");
            }
        });
        thread2.setDaemon(true);
        
        thread1.start();
        thread2.start();
        
        clientDriver.verify();
        
    }
    
    private static void schnooze(long interval, TimeUnit unit) {
        try {
            unit.sleep(interval);
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
