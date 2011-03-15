package com.nokia.batchprocessor.testbench.unit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.eclipse.jetty.server.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nokia.batchprocessor.testbench.BenchHandlerImpl;
import com.nokia.batchprocessor.testbench.BenchRequest;
import com.nokia.batchprocessor.testbench.BenchResponse;
import com.nokia.batchprocessor.testbench.BenchRuntimeException;
import com.nokia.batchprocessor.testbench.BenchServerRuntimeException;
import com.nokia.batchprocessor.testbench.RequestMatcher;

public class BenchHandlerTest {

	private BenchHandlerImpl sut;
	private RequestMatcher mockRequestMatcher;

	@Before
	public void before() {
		mockRequestMatcher = EasyMock.createMock(RequestMatcher.class);
		sut = new BenchHandlerImpl(mockRequestMatcher);
	}

	@After
	public void after() {
		// EasyMock.verify(mockRequestMatcher);
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

		sut.addExpectation(new BenchRequest("hmm"), new BenchResponse("mmm"));

		EasyMock.replay(mockRequestMatcher);

		sut.checkForUnexpectedRequests();

		try {
			sut.checkForUnmatchedExpectations();
			Assert.fail();
		} catch (final BenchRuntimeException bre) {
			Assert.assertEquals("1 unmatched expectation(s), first is: BenchRequest: GET hmm; ", bre.getMessage());
		}

	}

	/**
	 * with no expectations set, and a request made, the handler throws an error upon verification
	 */
	@Test
	public void testUnexpectedRequest() throws IOException, ServletException {

		final Request mockRequest = EasyMock.createMock(Request.class);
		final HttpServletRequest mockHttpRequest = EasyMock.createMock(HttpServletRequest.class);
		final HttpServletResponse mockHttpResponse = EasyMock.createMock(HttpServletResponse.class);

		EasyMock.expect(mockHttpRequest.getPathInfo()).andReturn("yarr");
		EasyMock.expect(mockHttpRequest.getQueryString()).andReturn("gooo=gredge");

		EasyMock.replay(mockHttpRequest);
		EasyMock.replay(mockHttpResponse);
		EasyMock.replay(mockRequestMatcher);

		try {
			sut.handle("", mockRequest, mockHttpRequest, mockHttpResponse);
			Assert.fail();
		} catch (final BenchServerRuntimeException bre) {
			Assert.assertEquals("Unexpected request: yarr?gooo=gredge", bre.getMessage());
		}

		EasyMock.verify(mockHttpRequest);
		EasyMock.verify(mockHttpResponse);

		try {
			sut.checkForUnexpectedRequests();
			Assert.fail();
		} catch (final BenchRuntimeException bre) {
			Assert.assertEquals("Unexpected request: yarr?gooo=gredge", bre.getMessage());
		}

	}

	/**
	 * with an expectation set, and a request made, the handler checks for a match and returns the match if one is found
	 */
	@Test
	public void testExpectedRequest() throws IOException, ServletException {

		final Request mockRequest = EasyMock.createMock(Request.class);
		final HttpServletRequest mockHttpRequest = new Request();
		final HttpServletResponse mockHttpResponse = EasyMock.createMock(HttpServletResponse.class);

		final BenchRequest realRequest = new BenchRequest("yarr").withParam("gooo", "gredge");
		final BenchResponse realResponse = new BenchResponse("lovely").withStatus(404).withContentType("fhieow")
				.withHeader("hhh", "JJJ");

		EasyMock.expect(mockRequestMatcher.isMatch(mockHttpRequest, realRequest)).andReturn(true);

		mockHttpResponse.setContentType("fhieow");
		mockHttpResponse.setStatus(404);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintWriter printWriter = new PrintWriter(baos);
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
