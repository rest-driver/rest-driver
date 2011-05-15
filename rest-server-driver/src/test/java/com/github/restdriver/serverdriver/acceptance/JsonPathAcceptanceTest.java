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
import com.github.restdriver.clientdriver.example.ClientDriverUnitTest;
import com.github.restdriver.serverdriver.http.response.Response;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static com.github.restdriver.serverdriver.Matchers.*;
import static com.github.restdriver.serverdriver.RestServerDriver.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JsonPathAcceptanceTest extends ClientDriverUnitTest {

    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = super.getClientDriver().getBaseUrl();
    }

    @Test
    public void jsonPathCanBeRunOverJsonResponse() {
        String jsonContent = makeJson(" { 'thing' : 'valuoid' } ");
        getClientDriver().addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent));
        Response response = get(baseUrl);

        assertThat(response.asJson(), hasJsonPath("$.thing", equalTo("valuoid")));
    }

    @Test
    public void moreComplexJsonPathCanBeRunOverJsonResponse() throws ParseException {
        String jsonContent = makeJson(" { 'thing' : { 'sub' : { 'subsub' : 'valutron' } } } ");
        getClientDriver().addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent));
        Response response = get(baseUrl);

        assertThat(response.asJson(), hasJsonPath("$..subsub", hasItem(equalTo("valutron"))));
    }

    @Test
    public void jsonPathWithConditional() {
        String jsonContent = makeJson(

                " { 'things' : " +
                        "[ { 'thing' : { 'a': 'one', 'c' : 100 } } , " +
                        "  { 'thing' : { 'a': 'two', 'c' : 150 } } ] } ");

        getClientDriver().addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse(jsonContent));
        Response response = get(baseUrl);

        assertThat(response.asJson(), hasJsonPath("$.things.thing[?(@.c > 125)].a", hasItem(equalTo("two"))));
    }



    private String makeJson(String fakeJson) {
        return fakeJson.replace("'", "\"");
    }
}
