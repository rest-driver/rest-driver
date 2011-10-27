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
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;

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
    
    @Test
    public void instantiationWithHttpRequestPopulatesCorrectly() throws IOException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        String expectedPathInfo = "someUrlPath";
        String expectedMethod = "GET";
        Enumeration<String> expectedHeaderNames = new Vector<String>(Arrays.asList("header1")).elements();
        
        String bodyContent = "bodyContent";
        String expectedContentType = "contentType";

        when(mockRequest.getPathInfo()).thenReturn(expectedPathInfo);
        when(mockRequest.getMethod()).thenReturn(expectedMethod);
        when(mockRequest.getQueryString()).thenReturn("hello=world");
        when(mockRequest.getHeaderNames()).thenReturn(expectedHeaderNames);
        when(mockRequest.getHeader("header1")).thenReturn("thisIsHeader1");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(bodyContent)));
        when(mockRequest.getContentType()).thenReturn(expectedContentType);

        ClientDriverRequest clientDriverRequest = new ClientDriverRequest(mockRequest);

        assertThat((String) clientDriverRequest.getPath(), is(expectedPathInfo));
        assertThat(clientDriverRequest.getMethod(), is(Method.GET));
        assertThat(clientDriverRequest.getParams().size(), is(1));
        assertThat((String) clientDriverRequest.getParams().get("hello").iterator().next(), is("world"));
        assertThat((String) clientDriverRequest.getHeaders().get("header1"), is("thisIsHeader1"));
        assertThat((String) clientDriverRequest.getBodyContentType(), is(expectedContentType));
    }
}
