package com.github.restdriver.clientdriver.integration;

import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

public class FailureReportingTest {
    
    @Rule
    public ClientDriverRule clientDriver = new ClientDriverRule();
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void clientDriverFailureIsReportedOverAssertionFailure() throws Exception {
        
        thrown.expect(ClientDriverFailedExpectationException.class);
        thrown.expectMessage("Unexpected request: /two");
        
        clientDriver.addExpectation(onRequestTo("/one"), giveEmptyResponse());
        
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(clientDriver.getBaseUrl() + "/two");
        
        HttpResponse response = client.execute(get);
        
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_NO_CONTENT));
        
    }
    
    @Test
    public void assertionFailureIsReportedCorrectly() throws Exception {
        
        thrown.expect(AssertionError.class);
        
        clientDriver.addExpectation(onRequestTo("/one"), giveEmptyResponse());
        
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(clientDriver.getBaseUrl() + "/one");
        
        HttpResponse response = client.execute(get);
        
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        
    }
    
}
