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

import static com.github.restdriver.serverdriver.RestServerDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.serverdriver.http.response.Response;

/**
 * User: mjg
 * Date: 07/05/11
 * Time: 21:29
 */
public class ResponseToStringAcceptanceTest {
    
    @Rule
    public ClientDriverRule driver = new ClientDriverRule();
    
    private String baseUrl;
    private final String n = SystemUtils.LINE_SEPARATOR;
    
    @Before
    public void getServerDetails() {
        baseUrl = driver.getBaseUrl();
    }
    
    @Test
    public void testToStringWithoutResponseBody() {
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse("").withStatus(400));
        
        Response response = get(baseUrl);
        
        String expectedResponse = "HTTP/1.1 400 Bad Request" + n;
        expectedResponse += "Content-Type: text/plain;charset=ISO-8859-1" + n +
                "Content-Length: 0" + n +
                "Server: Jetty(8.0.4.v20111024)";
        
        assertThat(response.toString(), is(equalTo(expectedResponse)));
        
    }
    
    @Test
    public void testToStringWithResponseBody() {
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse("This is the content"));
        
        Response response = get(baseUrl);
        
        String expectedResponse = "HTTP/1.1 200 OK" + n;
        expectedResponse += "Content-Type: text/plain;charset=ISO-8859-1" + n +
                "Content-Length: 19" + n +
                "Server: Jetty(8.0.4.v20111024)" + n +
                n +
                "This is the content";
        
        assertThat(response.toString(), is(equalTo(expectedResponse)));
        
    }
    
}
