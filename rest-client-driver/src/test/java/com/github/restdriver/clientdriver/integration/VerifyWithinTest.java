package com.github.restdriver.clientdriver.integration;

import static com.github.restdriver.clientdriver.RestClientDriver.*;

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
    public void verifyingSingleRequestWithinATimePeriodWorks() throws Exception {
        
        clientDriver.addExpectation(
                onRequestTo("/foo"),
                giveEmptyResponse().within(500, TimeUnit.MILLISECONDS));
        
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(200, TimeUnit.MILLISECONDS);
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
                giveEmptyResponse().within(500, TimeUnit.MILLISECONDS));
        
        clientDriver.addExpectation(
                onRequestTo("/foo2"),
                giveEmptyResponse().within(500, TimeUnit.MILLISECONDS));
        
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(200, TimeUnit.MILLISECONDS);
                hitThat(clientDriver.getBaseUrl() + "/foo1");
            }
        });
        thread1.setDaemon(true);
        thread1.start();
        
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(200, TimeUnit.MILLISECONDS);
                hitThat(clientDriver.getBaseUrl() + "/foo2");
            }
        });
        thread2.setDaemon(true);
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
        thread1.start();
        
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                schnooze(500, TimeUnit.MILLISECONDS);
                hitThat(clientDriver.getBaseUrl() + "/foo2");
            }
        });
        thread2.setDaemon(true);
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
