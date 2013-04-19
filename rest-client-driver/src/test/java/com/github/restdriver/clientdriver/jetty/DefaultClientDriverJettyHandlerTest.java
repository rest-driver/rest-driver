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
package com.github.restdriver.clientdriver.jetty;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.input.ReaderInputStream;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.RealRequest;
import com.github.restdriver.clientdriver.RequestMatcher;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.unit.DummyServletInputStream;

public class DefaultClientDriverJettyHandlerTest {
	private RequestMatcher mockRequestMatcher;
	private Request mockRequest;
	private HttpServletRequest mockHttpRequest;
	private HttpServletResponse mockHttpResponse;

	@Before
	public void before() {
		mockRequestMatcher = mock(RequestMatcher.class);
		mockRequest = mock(Request.class);
		mockHttpRequest = mock(Request.class);
		mockHttpResponse = mock(HttpServletResponse.class);
	}

	@SuppressWarnings("resource")
	@Test
	public void when_responseContainsBothBodyAndHeaders_headers_shouldBeSetBeforeBody_otherwise_theyWontBeSentAtAll() throws IOException, ServletException {
		ServletInputStream servletInputStream = new DummyServletInputStream(new ReaderInputStream(new StringReader("")));
		when(mockHttpRequest.getInputStream()).thenReturn(servletInputStream);

		ClientDriverRequest realRequest = new ClientDriverRequest("/").withMethod(Method.GET);
		ClientDriverResponse realResponse = new ClientDriverResponse("entity payload", "text/plain").withStatus(200).withHeader("Test", "header-should-be-set-before-writing-body");

		when(mockHttpRequest.getMethod()).thenReturn("GET");
		when(mockHttpRequest.getReader()).thenReturn(new BufferedReader(new StringReader("")));
		when(mockRequestMatcher.isMatch((RealRequest) anyObject(), (ClientDriverRequest) anyObject())).thenReturn(true);

		ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
		when(mockHttpResponse.getOutputStream()).thenReturn(servletOutputStream);

		DefaultClientDriverJettyHandler sut = new DefaultClientDriverJettyHandler(mockRequestMatcher);
		sut.addExpectation(realRequest, realResponse);
		sut.handle("", mockRequest, mockHttpRequest, mockHttpResponse);

		verify(mockHttpResponse).setStatus(200);

		InOrder inOrder = inOrder(mockHttpResponse, servletOutputStream);
		inOrder.verify(mockHttpResponse).setContentType("text/plain");
		inOrder.verify(mockHttpResponse).setHeader("Test", "header-should-be-set-before-writing-body");
		inOrder.verify(servletOutputStream).write("entity payload".getBytes("UTF-8"));
	}
}
