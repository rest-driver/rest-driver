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
package com.github.restdriver.clientdriver.unit;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

public class ClientDriverRequestTest {
    
    @Test
    public void usingWithHeaderCanOverrideBodyContentType() {
        ClientDriverRequest request = new ClientDriverRequest("/blah").withBody("BODY", "text/plain");
        
        assertThat(request.getBodyContentType().matches("text/plain"), is(true));
        
        request.withHeader("Content-Type", "text/xml");
        
        assertThat(request.getBodyContentType().matches("text/xml"), is(true));
    }
    
    @Test
    public void usingWithHeaderCanOverrideBodyContentTypeIgnoringCase() {
        ClientDriverRequest request = new ClientDriverRequest("/blah").withBody("BODY", "text/plain");
        
        assertThat(request.getBodyContentType().matches("text/plain"), is(true));
        
        request.withHeader("content-type", "text/xml");
        
        assertThat(request.getBodyContentType().matches("text/xml"), is(true));
    }
    
    @Test
    public void toStringIncludesPath() {
        ClientDriverRequest request = new ClientDriverRequest("/blah");
        assertThat(request.toString(), containsString("/blah"));

        request = new ClientDriverRequest(containsString("/lalame"));
        assertThat(request.toString(), containsString("string containing \"/lalame\""));
    }
    
    @Test
    public void toStringIncludesMethod() {
        ClientDriverRequest request = new ClientDriverRequest("/blah").withMethod(Method.POST);
        assertThat(request.toString(), containsString("POST"));
    }
    
    @Test
    public void toStringIncludesParams() {
        ClientDriverRequest request = new ClientDriverRequest("/blah").withParam("q", "something").withParam("rows", "10");
        assertThat(request.toString(), containsString("PARAMS: [q=[\"something\"],rows=[\"10\"]]"));

        request = new ClientDriverRequest("/blah").withParam("q", containsString("something"));
        assertThat(request.toString(), containsString("PARAMS: [q=[a string containing \"something\"]]"));
    }

    @Test
    public void toStringIncludesHeaders() {
        ClientDriverRequest request =
            new ClientDriverRequest("/blah")
                .withHeader("test-me", startsWith("more_test"))
                .withoutHeader("excluded-header");

        assertThat(request.toString(), containsString("HEADERS: [test-me: a string starting with \"more_test\""));
        assertThat(request.toString(), containsString("NOT HEADERS: [excluded-header"));
    }
}
