package com.googlecode.rd.clientdriver.unit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.rd.clientdriver.DefaultRequestMatcher;
import com.googlecode.rd.types.ClientDriverRequest;
import com.googlecode.rd.types.ClientDriverRequest.Method;

public class RequestMatcherTest {

	private DefaultRequestMatcher sut;
	private HttpServletRequest aReq;

	@Before
	public void before() {
		sut = new DefaultRequestMatcher();
	}

	@After
	public void after() {
		EasyMock.verify(aReq);
	}

	@Test
	public void testMatchNoParams() {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(new HashMap<Object, Object>());

		EasyMock.replay(aReq);

		Assert.assertTrue(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchNoParamsPattern() {

		final ClientDriverRequest bReq = new ClientDriverRequest(Pattern.compile("[a]{5}")).withMethod(Method.GET);
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(new HashMap<Object, Object>());

		EasyMock.replay(aReq);

		Assert.assertTrue(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithParams() {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa")
										.withMethod(Method.GET)
										.withParam("kk", "vv")
										.withParam("k2", "v2");

		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(2));
		EasyMock.expect(aReq.getParameter("kk")).andReturn("vv");
		EasyMock.expect(aReq.getParameter("k2")).andReturn("v2");

		EasyMock.replay(aReq);

		Assert.assertTrue(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithParamsPattern() {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk",
				Pattern.compile("[v]{2}")).withParam("k2", Pattern.compile("v[0-9]"));
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(2));
		EasyMock.expect(aReq.getParameter("kk")).andReturn("vv");
		EasyMock.expect(aReq.getParameter("k2")).andReturn("v2");

		EasyMock.replay(aReq);

		Assert.assertTrue(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithWrongParam() {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv");
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(1));
		EasyMock.expect(aReq.getParameter("kk")).andReturn("not vv");

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithWrongParamPattern() {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk",
				Pattern.compile("[v]{2}"));
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(1));
		EasyMock.expect(aReq.getParameter("kk")).andReturn("xx");

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithNullParam() {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv");
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(1));
		EasyMock.expect(aReq.getParameter("kk")).andReturn(null);

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map getMapOfSize(final int size) {
		final Map mockMap = new HashMap();
		for (int i = 0; i < size; i++) {
			mockMap.put("k" + i, "v" + i);
		}
		return mockMap;
	}

	@Test
	public void testMatchWithParamsTooMany() {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv").withParam(
				"k2", "v2");
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(1));

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithParamsTooFew() {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv");
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(2));

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWrongMethod() {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.DELETE);
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getMethod()).andReturn("GET");

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWrongPath() {

		final ClientDriverRequest bReq = new ClientDriverRequest("bbbbb").withMethod(Method.GET);
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWrongPathPattern() {

		final ClientDriverRequest bReq = new ClientDriverRequest(Pattern.compile("[b]{5}")).withMethod(Method.GET);
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithRequestBody() throws IOException {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(0));

		EasyMock.expect(aReq.getContentType()).andReturn("text/junk");

		final BufferedReader contentReader = new BufferedReader(new StringReader("ooooh"));
		EasyMock.expect(aReq.getReader()).andReturn(contentReader);

		EasyMock.replay(aReq);

		Assert.assertTrue(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithRequestBodyPattern() throws IOException {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody(Pattern.compile("[o]{4}h"),
				Pattern.compile("text/j[a-z]{3}"));
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(0));

		EasyMock.expect(aReq.getContentType()).andReturn("text/junk");

		final BufferedReader contentReader = new BufferedReader(new StringReader("ooooh"));
		EasyMock.expect(aReq.getReader()).andReturn(contentReader);

		EasyMock.replay(aReq);

		Assert.assertTrue(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithRequestBodyWrongType() throws IOException {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(0));

		EasyMock.expect(aReq.getContentType()).andReturn("text/jnkular");

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithRequestBodyWrongTypePattern() throws IOException {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh",
				Pattern.compile("text/[a-z]{4}"));
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(0));

		EasyMock.expect(aReq.getContentType()).andReturn("text/jnkular");

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithRequestBodyWrongContent() throws IOException {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(0));

		EasyMock.expect(aReq.getContentType()).andReturn("text/junk");

		final BufferedReader contentReader = new BufferedReader(new StringReader("ooook"));
		EasyMock.expect(aReq.getReader()).andReturn(contentReader);

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

	@Test
	public void testMatchWithRequestBodyWrongContentPattern() throws IOException {

		final ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody(Pattern.compile("[o]{4}h"),
				"text/junk");
		aReq = EasyMock.createMock(HttpServletRequest.class);

		EasyMock.expect(aReq.getPathInfo()).andReturn("aaaaa");
		EasyMock.expect(aReq.getMethod()).andReturn("GET");
		EasyMock.expect(aReq.getParameterMap()).andReturn(getMapOfSize(0));

		EasyMock.expect(aReq.getContentType()).andReturn("text/junk");

		final BufferedReader contentReader = new BufferedReader(new StringReader("ooook"));
		EasyMock.expect(aReq.getReader()).andReturn(contentReader);

		EasyMock.replay(aReq);

		Assert.assertFalse(sut.isMatch(aReq, bReq));

	}

}
