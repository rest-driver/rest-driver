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
        thrown.expectMessage("Unexpected request(s): [GET /two]");
        
        clientDriver.addExpectation(onRequestTo("/one"), giveEmptyResponse());
        
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(clientDriver.getBaseUrl() + "/two");
        
        HttpResponse response = client.execute(get);
        
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        
    }
    
    @Test
    public void assertionFailureIsReportedCorrectly() throws Exception {
        
        thrown.handleAssertionErrors();
        thrown.expect(AssertionError.class);
        
        clientDriver.addExpectation(onRequestTo("/one"), giveEmptyResponse());
        
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(clientDriver.getBaseUrl() + "/one");
        
        HttpResponse response = client.execute(get);
        
        // We assert for a 500 response code rather than the 204 we've specified above to generate an AssertionError via Hamcrest.
        // This AssertionError is then expected by the ExpectedException @Rule above.
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        
    }
    
}
