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

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.serverdriver.http.response.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.restdriver.serverdriver.Matchers.*;
import static com.github.restdriver.serverdriver.RestServerDriver.get;
import static com.github.restdriver.serverdriver.RestServerDriver.header;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PollingAcceptanceTest {

    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = driver.getBaseUrl();
    }

    @Test
    public void simpleGetRetrievesStatusAndContent() {
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse("Content1"));
        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse("Content2"));

        System.out.println(get(baseUrl));
        System.out.println(get(baseUrl));

    }

}
