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
package com.github.restdriver.clientdriver.clientdriver.unit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.RequestMatcher;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.clientdriver.jetty.DefaultClientDriverJettyHandler;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.eclipse.jetty.server.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.restdriver.clientdriver.exception.ClientDriverInternalException;

public class BenchHandlerTest {

    private DefaultClientDriverJettyHandler sut;
    private RequestMatcher mockRequestMatcher;

    @Before
    public void before() {
        mockRequestMatcher = EasyMock.createMock(RequestMatcher.class);
        sut = new DefaultClientDriverJettyHandler(mockRequestMatcher);
    }

    @After
    public void after() {
        EasyMock.verify(mockRequestMatcher);
    }

    /**
     * with no expectations set, and no requests made, the handler does not report any errors
     */
    @Test
    public void testMinimalHandler() {

        EasyMock.replay(mockRequestMatcher);

        sut.checkForUnexpectedRequests();
        sut.checkForUnmatchedExpectations();

    }

    /**
     * with expectations set, and no requests made, the handler throws an error upon verification
     */
    @Test
    public void testUnmetExpectation() {

        sut.addExpectation(new ClientDriverRequest("hmm"), new ClientDriverResponse("mmm"));

        EasyMock.replay(mockRequestMatcher);

        sut.checkForUnexpectedRequests();

        try {
            sut.checkForUnmatchedExpectations();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            Assert.assertEquals("1 unmatched expectation(s), first is: BenchRequest: GET hmm; ", bre.getMessage());
        }

    }

    /**
     * with no expectations set, and a request made, the handler throws an error upon verification
     */
    @Test
    public void testUnexpectedRequest() throws IOException, ServletException {

        Request mockRequest = EasyMock.createMock(Request.class);
        HttpServletRequest mockHttpRequest = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse mockHttpResponse = EasyMock.createMock(HttpServletResponse.class);

        EasyMock.expect(mockHttpRequest.getPathInfo()).andReturn("yarr");
        EasyMock.expect(mockHttpRequest.getQueryString()).andReturn("gooo=gredge");

        EasyMock.replay(mockHttpRequest);
        EasyMock.replay(mockHttpResponse);
        EasyMock.replay(mockRequestMatcher);

        try {
            sut.handle("", mockRequest, mockHttpRequest, mockHttpResponse);
            Assert.fail();
        } catch (ClientDriverInternalException bre) {
            Assert.assertEquals("Unexpected request: yarr?gooo=gredge", bre.getMessage());
        }

        EasyMock.verify(mockHttpRequest);
        EasyMock.verify(mockHttpResponse);

        try {
            sut.checkForUnexpectedRequests();
            Assert.fail();
        } catch (ClientDriverFailedExpectationException bre) {
            Assert.assertEquals("Unexpected request: yarr?gooo=gredge", bre.getMessage());
        }

    }

    /**
     * with an expectation set, and a request made, the handler checks for a match and returns the match if one is found
     */
    @Test
    public void testExpectedRequest() throws IOException, ServletException {

        Request mockRequest = EasyMock.createMock(Request.class);
        HttpServletRequest mockHttpRequest = new Request();
        HttpServletResponse mockHttpResponse = EasyMock.createMock(HttpServletResponse.class);

        ClientDriverRequest realRequest = new ClientDriverRequest("yarr").withParam("gooo", "gredge");
        ClientDriverResponse realResponse = new ClientDriverResponse("lovely").withStatus(404).withContentType("fhieow")
                .withHeader("hhh", "JJJ");

        EasyMock.expect(mockRequestMatcher.isMatch(mockHttpRequest, realRequest)).andReturn(true);

        mockHttpResponse.setContentType("fhieow");
        mockHttpResponse.setStatus(404);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(baos);

        EasyMock.expect(mockHttpResponse.getWriter()).andReturn(printWriter);
        mockHttpResponse.setHeader("hhh", "JJJ");

        EasyMock.replay(mockHttpResponse);
        EasyMock.replay(mockRequestMatcher);

        sut.addExpectation(realRequest, realResponse);

        sut.getJettyHandler().handle("", mockRequest, mockHttpRequest, mockHttpResponse);

        EasyMock.verify(mockHttpResponse);

        printWriter.close();
        Assert.assertEquals("lovely", new String(baos.toByteArray()));

    }
}
