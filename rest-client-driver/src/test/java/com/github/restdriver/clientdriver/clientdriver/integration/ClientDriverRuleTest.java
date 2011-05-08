/**
 * Copyright ¬© 2010-2011 Nokia
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
package com.github.restdriver.clientdriver.clientdriver.integration;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

public class ClientDriverRuleTest {

    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void letsTrySomethingThatFails() throws Exception {

        // We use ExpectedException to catch the exception we (hopefully) get because the expectations weren't met
        thrown.expect(ClientDriverFailedExpectationException.class);

        driver.expect(new ClientDriverRequest("/blah"), new ClientDriverResponse("OUCH!!").withStatus(200));
        driver.expect(new ClientDriverRequest("/blah"), new ClientDriverResponse("OUCH!!").withStatus(404));

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(driver.getBaseUrl() + "/blah?gang=groon");

        client.execute(post);

    }

    @Test
    public void letsTrySomethingThatWorks() throws Exception {

        driver.expect(new ClientDriverRequest("/blah"), new ClientDriverResponse("").withStatus(404));

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(driver.getBaseUrl() + "/blah");

        client.execute(get);

    }

}
