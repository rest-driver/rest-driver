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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.serverdriver.http.response.Response;

public class OptionsAcceptanceTest {
    
    @Rule
    public ClientDriverRule driver = new ClientDriverRule();
    
    private String baseUrl;
    
    @Before
    public void getServerDetails() {
        baseUrl = driver.getBaseUrl();
    }
    
    @Test
    public void optionsRequestIsSuccessful() {
        driver.addExpectation(
                new ClientDriverRequest("/").withMethod(Method.OPTIONS),
                new ClientDriverResponse((String) null).withHeader("Allow", "GET, DELETE, OPTIONS"));
        
        Response response = options(baseUrl);
        
        assertThat(response, hasStatusCode(204));
        assertThat(response, hasHeaderWithValue("Allow", containsString("GET")));
        assertThat(response, hasHeaderWithValue("Allow", containsString("DELETE")));
        assertThat(response, hasHeaderWithValue("Allow", containsString("OPTIONS")));
        
        assertThat(response, hasHeaderWithValue("Allow", not(containsString("PUT"))));
        assertThat(response, hasHeaderWithValue("Allow", not(containsString("POST"))));
        assertThat(response, hasHeaderWithValue("Allow", not(containsString("HEAD"))));
        assertThat(response, hasHeaderWithValue("Allow", not(containsString("TRACE"))));
        
    }
}
