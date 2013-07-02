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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.HttpRealRequest;
import com.github.restdriver.clientdriver.RealRequest;

public class HttpRealRequestTest {
    
    @Test
    public void instantiationWithHttpRequestPopulatesCorrectly() throws IOException {
        
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        String expectedPathInfo = "someUrlPath";
        String expectedMethod = "GET";
        Enumeration<String> expectedHeaderNames = Collections.enumeration(Arrays.asList("header1"));
        
        String bodyContent = "bodyContent";
        String expectedContentType = "contentType";
        
        when(mockRequest.getPathInfo()).thenReturn(expectedPathInfo);
        when(mockRequest.getMethod()).thenReturn(expectedMethod);
        when(mockRequest.getQueryString()).thenReturn("hello=world");
        when(mockRequest.getHeaderNames()).thenReturn(expectedHeaderNames);
        when(mockRequest.getHeader("header1")).thenReturn("thisIsHeader1");
        when(mockRequest.getInputStream()).thenReturn(new DummyServletInputStream(IOUtils.toInputStream(bodyContent)));
        when(mockRequest.getContentType()).thenReturn(expectedContentType);
        
        RealRequest realRequest = new HttpRealRequest(mockRequest);
        
        assertThat((String) realRequest.getPath(), is(expectedPathInfo));
        assertThat(realRequest.getMethod(), is(Method.GET));
        assertThat(realRequest.getParams().size(), is(1));
        assertThat((String) realRequest.getParams().get("hello").iterator().next(), is("world"));
        assertThat((String) realRequest.getHeaders().get("header1"), is("thisIsHeader1"));
        assertThat((String) realRequest.getBodyContentType(), is(expectedContentType));
        
    }
    
}
