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
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static com.github.restdriver.serverdriver.Matchers.*;
import static com.github.restdriver.serverdriver.RestServerDriver.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class Rfc1123DatesInHeadersAcceptanceTest {

    private final String compliantDate = "Mon, 09 May 2011 18:49:18 GMT";
    private final String unCompliantDate = "Junk, 09 May 2011 18:49:18 GMT";
    private final String compliantButInvalidDate = "Mon, 12 May 2011 18:49:18 GMT"; // was not a Monday

    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    @Test
    public void assertOnValidDateHeader() {

        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse().withHeader("Date", compliantDate));
        Response response = get(driver.getBaseUrl());

        assertThat( response.getHeader("Date"), isValidDateHeader() );
        assertThat( response.getHeader("Date"), isRfc1123Compliant() );

    }

    @Test
    public void assertOnInvalidFormatDateHeader() {

        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse().withHeader("Date", unCompliantDate));
        Response response = get(driver.getBaseUrl());

        assertThat( response.getHeader("Date"), not(isValidDateHeader()) );
        assertThat( response.getHeader("Date"), not(isRfc1123Compliant()) );

    }

    @Test
    public void assertOnInvalidDateHeader() {

        driver.addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse().withHeader("Date", compliantButInvalidDate));
        Response response = get(driver.getBaseUrl());

        assertThat( response.getHeader("Date"), not(isValidDateHeader()) );
        assertThat( response.getHeader("Date"), not(isRfc1123Compliant()) );

    }

    @Test
    public void assertOnDateHeaderInThePast() {

        

    }


}
