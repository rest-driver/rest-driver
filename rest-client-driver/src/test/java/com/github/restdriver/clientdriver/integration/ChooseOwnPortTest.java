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
import java.net.BindException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import com.github.restdriver.clientdriver.exception.ClientDriverSetupException;

/**
 * User: mjg
 * Date: 25/05/11
 * Time: 20:51
 */
public class ChooseOwnPortTest {
    
    @Test
    public void userCanChooseOwnPort() throws IOException {
        
        int portNum = ClientDriver.getFreePort();
        
        ClientDriver driver = new ClientDriverFactory().createClientDriver(portNum);
        driver.addExpectation(onRequestTo("/url"), giveResponse("hello"));
        
        HttpClient client = new DefaultHttpClient();
        HttpGet getter = new HttpGet("http://localhost:" + portNum + "/url");
        HttpResponse response = client.execute(getter);
        
        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(IOUtils.toString(response.getEntity().getContent()), is("hello"));
        
    }
    
    @Test
    public void correctExceptionIsThrownIfPortIsUnavailable() throws IOException {
        
        int portNum = ClientDriver.getFreePort();
        
        try {
            // one of these must throw an exception.
            new ClientDriverFactory().createClientDriver(portNum);
            new ClientDriverFactory().createClientDriver(portNum);
            
        } catch (ClientDriverSetupException cdse) {
            
            assertThat(cdse.getCause(), instanceOf(BindException.class));
            
        }
        
    }
    
}
