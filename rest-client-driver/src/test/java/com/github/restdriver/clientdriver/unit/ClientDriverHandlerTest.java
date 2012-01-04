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

import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.RealRequest;
import com.github.restdriver.clientdriver.RequestMatcher;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.clientdriver.exception.ClientDriverInternalException;
import com.github.restdriver.clientdriver.jetty.DefaultClientDriverJettyHandler;

public class ClientDriverHandlerTest {
    
    private DefaultClientDriverJettyHandler sut;
    private RequestMatcher mockRequestMatcher;
    
    @Before
    public void before() {
        mockRequestMatcher = mock(RequestMatcher.class);
        sut = new DefaultClientDriverJettyHandler(mockRequestMatcher);
    }
    
    /**
     * with no expectations set, and no requests made, the handler does not report any errors
     */
    @Test
    public void testMinimalHandler() {
        
        sut.checkForUnexpectedRequests();
        sut.checkForUnmatchedExpectations();
        
    }
    
    /**
     * with expectations set, and no requests made, the handler throws an error upon verification
     */
    @Test
    public void testUnmetExpectation() {
        
        sut.addExpectation(onRequestTo("hmm"), giveResponse("mmm"));
        
        sut.checkForUnexpectedRequests();
        
        try {
            sut.checkForUnmatchedExpectations();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), equalTo("1 unmatched expectation(s), first is: ClientDriverRequest: GET hmm; expected: 1, actual: 0"));
        }
        
    }
    
    /**
     * with no expectations set, and a request made, the handler throws an error upon verification
     */
    @Test
    public void testUnexpectedRequest() throws IOException, ServletException {
        
        Request mockRequest = mock(Request.class);
        HttpServletRequest mockHttpRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockHttpResponse = mock(HttpServletResponse.class);
        
        when(mockHttpRequest.getMethod()).thenReturn("POST");
        when(mockHttpRequest.getPathInfo()).thenReturn("yarr");
        when(mockHttpRequest.getQueryString()).thenReturn("gooo=gredge");
        when(mockHttpRequest.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        
        try {
            sut.handle("", mockRequest, mockHttpRequest, mockHttpResponse);
            Assert.fail();
        } catch (ClientDriverInternalException e) {
            assertThat(e.getMessage(), equalTo("Unexpected request: POST yarr?gooo=gredge"));
        }
        
        try {
            sut.checkForUnexpectedRequests();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException e) {
            assertThat(e.getMessage(), equalTo("Unexpected request: POST yarr?gooo=gredge"));
        }
        
    }
    
    /**
     * with an expectation set, and a request made, the handler checks for a match and returns the match if one is found
     */
    public void testExpectedRequest() throws IOException, ServletException {
        
        Request mockRequest = mock(Request.class);
        HttpServletRequest mockHttpRequest = mock(Request.class);
        HttpServletResponse mockHttpResponse = mock(HttpServletResponse.class);
        
        ClientDriverRequest realRequest = new ClientDriverRequest("yarr").withMethod(Method.GET).withParam("gooo", "gredge");
        ClientDriverResponse realResponse = new ClientDriverResponse("lovely").withStatus(404).withContentType("fhieow").withHeader("hhh", "JJJ");
        
        when(mockHttpRequest.getMethod()).thenReturn("GET");
        when(mockHttpRequest.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        when(mockRequestMatcher.isMatch((RealRequest) anyObject(), (ClientDriverRequest) anyObject())).thenReturn(true);
        
        mockHttpResponse.setContentType("fhieow");
        mockHttpResponse.setStatus(404);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(baos);
        
        when(mockHttpResponse.getWriter()).thenReturn(printWriter);
        mockHttpResponse.setHeader("hhh", "JJJ");
        
        sut.addExpectation(realRequest, realResponse);
        sut.handle("", mockRequest, mockHttpRequest, mockHttpResponse);
        
        printWriter.close();
        assertThat(new String(baos.toByteArray()), equalTo("lovely"));
    }
}
