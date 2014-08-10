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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
        
        sut.addExpectation(onRequestTo("hmm"), giveResponse("mmm", "text/plain"));
        
        sut.checkForUnexpectedRequests();
        
        try {
            sut.checkForUnmatchedExpectations();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            assertThat(bre.getMessage(), containsString("1 unmatched expectation(s):"));
            assertThat(bre.getMessage(), containsString("expected: 1, actual: 0 -> ClientDriverRequest: GET \"hmm\";"));
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
        when(mockHttpRequest.getInputStream()).thenReturn(new DummyServletInputStream(IOUtils.toInputStream("")));
        
        try {
            sut.handle("", mockRequest, mockHttpRequest, mockHttpResponse);
            Assert.fail();
        } catch (ClientDriverFailedExpectationException e) {
            assertThat(e.getMessage(), containsString("1 unexpected request(s):"));
            assertThat(e.getMessage(), containsString("POST yarr; PARAMS: [gooo=[gredge]];"));
        }
    }
    
    /**
     * with an expectation set, and a request made, the handler checks for a match and returns the match if one is found
     */
    @Test
    public void testExpectedRequest() throws IOException, ServletException {
        
        Request mockRequest = mock(Request.class);
        HttpServletRequest mockHttpRequest = mock(Request.class);
        HttpServletResponse mockHttpResponse = mock(HttpServletResponse.class);
        
        ClientDriverRequest realRequest = new ClientDriverRequest("yarr").withMethod(Method.GET).withParam("gooo", "gredge");
        ClientDriverResponse realResponse = new ClientDriverResponse("lovely", "fhieow").withStatus(404).withHeader("hhh", "JJJ");
        
        when(mockHttpRequest.getMethod()).thenReturn("GET");
        when(mockHttpRequest.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        when(mockHttpRequest.getInputStream()).thenReturn(new DummyServletInputStream(new ByteArrayInputStream("".getBytes())));
        when(mockRequestMatcher.isMatch((RealRequest) anyObject(), (ClientDriverRequest) anyObject())).thenReturn(true);
        
        mockHttpResponse.setContentType("fhieow");
        mockHttpResponse.setStatus(404);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(mockHttpResponse.getOutputStream()).thenReturn(new DummyServletOutputStream(baos));
        
        sut.addExpectation(realRequest, realResponse);
        sut.handle("", mockRequest, mockHttpRequest, mockHttpResponse);
        
        assertThat(new String(baos.toByteArray()), equalTo("lovely"));
    }
    
    private static class DummyServletOutputStream extends ServletOutputStream {
        private final OutputStream outputStream;
        
        public DummyServletOutputStream(OutputStream baos) {
            outputStream = baos;
        }
        
        @Override
        public boolean isReady() {
            return false;
        }
        
        @Override
        public void setWriteListener(WriteListener writeListener) {
            
        }
        
        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }
    }
}
