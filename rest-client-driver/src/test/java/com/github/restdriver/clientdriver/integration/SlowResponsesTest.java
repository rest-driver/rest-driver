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

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.TimeUnit;

import static com.github.restdriver.clientdriver.RestClientDriver.giveEmptyResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;

import static java.util.concurrent.TimeUnit.*;

public class SlowResponsesTest {

    @Test
    public void notSpecifyingExpectationNumberDefaultsToOnce() throws Exception {

        ClientDriver driver = new ClientDriverFactory().createClientDriver();
        driver.addExpectation(onRequestTo("/request"), giveEmptyResponse().after(250, MILLISECONDS));

        HttpClient client = new DefaultHttpClient();
        HttpGet getter = new HttpGet(driver.getBaseUrl() + "/request");

        client.execute(getter);

        // Nothing to assert here without relying on system clock and having a long running test :(

        driver.shutdown();
    }

}
