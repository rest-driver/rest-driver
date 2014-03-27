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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

public class SpecifyExpectationNumberTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void notSpecifyingExpectationNumberDefaultsToOnce() throws Exception {
        
        ClientDriver driver = new ClientDriverFactory().createClientDriver();
        driver.addExpectation(onRequestTo("/request"), giveEmptyResponse());
        
        HttpClient client = new DefaultHttpClient();
        HttpGet getter = new HttpGet(driver.getBaseUrl() + "/request");
        client.execute(getter);
        
        driver.shutdown();
        
    }
    
    @Test
    public void specifyingNumberOfTimesForExpectationExpectsItThatNumberOfTimes() throws Exception {
        
        ClientDriver driver = new ClientDriverFactory().createClientDriver();
        driver.addExpectation(onRequestTo("/request"), giveEmptyResponse()).times(2);
        
        HttpGet getter = new HttpGet(driver.getBaseUrl() + "/request");
        new DefaultHttpClient().execute(getter);
        new DefaultHttpClient().execute(getter);
        
        driver.shutdown();
        
    }
    
    @Test
    public void specifyingNumberOfTimesForExpectationAndNotRequestingCorrectNumberOfTimesFails() throws Exception {
        
        thrown.expect(ClientDriverFailedExpectationException.class);
        
        ClientDriver driver = new ClientDriverFactory().createClientDriver();
        driver.addExpectation(onRequestTo("/request"), giveEmptyResponse()).times(2);
        
        HttpGet getter = new HttpGet(driver.getBaseUrl() + "/request");
        new DefaultHttpClient().execute(getter);
        new DefaultHttpClient().execute(getter);
        new DefaultHttpClient().execute(getter);
        
        driver.shutdown();
        
    }
    
    @Test
    public void specifyingNumberOfTimesFailureHasDecentMessage() throws Exception {
        
        thrown.expect(ClientDriverFailedExpectationException.class);
        thrown.expectMessage("1 unmatched expectation(s):");
        thrown.expectMessage("expected: 2, actual: 1 -> ClientDriverRequest: GET \"/request\";");
        
        ClientDriver driver = new ClientDriverFactory().createClientDriver();
        driver.addExpectation(onRequestTo("/request"), giveEmptyResponse()).times(2);
        
        HttpGet getter = new HttpGet(driver.getBaseUrl() + "/request");
        new DefaultHttpClient().execute(getter);
        
        driver.shutdown();
        
    }
    
    @Test
    public void specifyingAnyTimesForExpectationWorks() throws Exception {
        
        ClientDriver driver = new ClientDriverFactory().createClientDriver();
        driver.addExpectation(onRequestTo("/request"), giveEmptyResponse()).anyTimes();
        
        HttpGet getter = new HttpGet(driver.getBaseUrl() + "/request");
        new DefaultHttpClient().execute(getter);
        new DefaultHttpClient().execute(getter);
        new DefaultHttpClient().execute(getter);
        
        driver.shutdown();
        
    }
    
    @Test
    public void specifyingAnyTimesForExpectationAnyNotCallingItWorks() throws Exception {
        
        ClientDriver driver = new ClientDriverFactory().createClientDriver();
        driver.addExpectation(onRequestTo("/request"), giveEmptyResponse()).anyTimes();
        
        driver.shutdown();
        
    }
    
    @Test
    public void specifyingAnyTimesBeforeOtherExpectationsConsidersLaterExpectationsCorrectly() throws Exception {
        
        thrown.expect(ClientDriverFailedExpectationException.class);
        
        ClientDriver driver = new ClientDriverFactory().createClientDriver();
        driver.addExpectation(onRequestTo("/anytimes"), giveEmptyResponse()).anyTimes();
        driver.addExpectation(onRequestTo("/one"), giveEmptyResponse());
        
        driver.shutdown();
        
    }
    
}
