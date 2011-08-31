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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;

public class ClientDriverRequestTest {
    
    @Test
    public void usingWithHeaderCanOverrideBodyContentType() {
        ClientDriverRequest request = new ClientDriverRequest("/blah").withBody("BODY", "text/plain");
        
        assertThat(request.getBodyContentType().toString(), is("text/plain"));
        
        request.withHeader("Content-Type", "text/xml");
        
        assertThat(request.getBodyContentType().toString(), is("text/xml"));
    }
    
    @Test
    public void usingWithHeaderCanOverrideBodyContentTypeIgnoringCase() {
        ClientDriverRequest request = new ClientDriverRequest("/blah").withBody("BODY", "text/plain");
        
        assertThat(request.getBodyContentType().toString(), is("text/plain"));
        
        request.withHeader("content-type", "text/xml");
        
        assertThat(request.getBodyContentType().toString(), is("text/xml"));
    }
    
}
