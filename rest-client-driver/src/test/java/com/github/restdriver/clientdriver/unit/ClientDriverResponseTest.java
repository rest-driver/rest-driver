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

import com.github.restdriver.clientdriver.ClientDriverResponse;

public class ClientDriverResponseTest {
    
    @Test
    public void creatingResponseWithoutContentGives204Status() {
        ClientDriverResponse response = new ClientDriverResponse();
        
        assertThat(response.getStatus(), is(204));
    }
    
    @Test
    public void creatingResponseWithNullContentGives204Status() {
        ClientDriverResponse response = new ClientDriverResponse(null);
        
        assertThat(response.getStatus(), is(204));
    }
    
    @Test
    public void creatingResponseWithEmptyStringContentGives200Status() {
        ClientDriverResponse response = new ClientDriverResponse("");
        
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void creatingEmptyResponseGivesNoContentType() {
        
        assertThat(new ClientDriverResponse().getContentType(), is(nullValue()));
        assertThat(new ClientDriverResponse(null).getContentType(), is(nullValue()));
        assertThat(new ClientDriverResponse("").getContentType(), is(nullValue()));
        
    }

    @Test
    public void usingHeaderCanOverrideContentType() {
        ClientDriverResponse response = new ClientDriverResponse("hello").withContentType("text/plain");
        
        assertThat(response.getContentType(), is("text/plain"));
        
        response.withHeader("Content-Type", "text/xml");
        
        assertThat(response.getContentType(), is("text/xml"));
    }
    
    @Test
    public void usingHeaderCanOverrideContentTypeIgnoringCase() {
        ClientDriverResponse response = new ClientDriverResponse("hello").withContentType("text/plain");
        
        assertThat(response.getContentType(), is("text/plain"));
        
        response.withHeader("content-type", "text/xml");
        
        assertThat(response.getContentType(), is("text/xml"));
    }
    
}
