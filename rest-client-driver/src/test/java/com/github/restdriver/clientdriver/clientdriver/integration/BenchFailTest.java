package com.github.restdriver.clientdriver.clientdriver.integration;

import java.util.regex.Pattern;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import com.github.restdriver.types.ClientDriverRequest;
import com.github.restdriver.types.ClientDriverResponse;
import com.github.restdriver.types.ClientDriverRequest.Method;

public class BenchFailTest {

	private ClientDriver bServer;

	@Test
	public void testUnexpectedCall() throws Exception {
		bServer = new ClientDriverFactory().createClientDriver();

		// No expectations defined

		final HttpClient client = new DefaultHttpClient();
		final HttpGet getter = new HttpGet(bServer.getBaseUrl() + "/blah?foo=bar");

		client.execute(getter);

		try {
			bServer.shutdown();
			Assert.fail();
		} catch (final ClientDriverFailedExpectationException bre) {
			Assert.assertEquals("Unexpected request: /blah?foo=bar", bre.getMessage());
		}

	}

	@Test
	public void testUnmatchedExpectation() throws Exception {
		bServer = new ClientDriverFactory().createClientDriver();

		bServer.addExpectation(new ClientDriverRequest("/blah"), new ClientDriverResponse("OUCH!!").withStatus(200));
		bServer.addExpectation(new ClientDriverRequest("/blah"), new ClientDriverResponse("OUCH!!").withStatus(404));

		// no requests made

		try {
			bServer.shutdown();
			Assert.fail();
		} catch (final ClientDriverFailedExpectationException bre) {
			Assert.assertEquals("2 unmatched expectation(s), first is: BenchRequest: GET /blah; ", bre.getMessage());
		}

	}

	@Test
	public void testJettyWorkingWithMethodButIncorrectParams() throws Exception {
		bServer = new ClientDriverFactory().createClientDriver();

		bServer.addExpectation(new ClientDriverRequest("/blah").withMethod(Method.POST).withParam("gang", "green"),
				new ClientDriverResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
						"TestServer"));

		final HttpClient client = new DefaultHttpClient();

		final String baseUrl = bServer.getBaseUrl();
		final HttpPost poster = new HttpPost(baseUrl + "/blah?gang=groon");

		client.execute(poster);

		try {
			bServer.shutdown();
			Assert.fail();
		} catch (final ClientDriverFailedExpectationException bre) {
			Assert.assertEquals("Unexpected request: /blah?gang=groon", bre.getMessage());
		}

	}

	@Test
	public void testJettyWorkingWithMethodButIncorrectParamsPattern() throws Exception {
		bServer = new ClientDriverFactory().createClientDriver();

		bServer.addExpectation(new ClientDriverRequest(Pattern.compile("/b[a-z]{3}")).withMethod(Method.POST).withParam(
				"gang", Pattern.compile("r")), new ClientDriverResponse("OUCH!!").withStatus(200)
				.withContentType("text/plain").withHeader("Server", "TestServer"));

		final HttpClient client = new DefaultHttpClient();

		final String baseUrl = bServer.getBaseUrl();
		final HttpPost poster = new HttpPost(baseUrl + "/blah?gang=goon");

		client.execute(poster);

		try {
			bServer.shutdown();
			Assert.fail();
		} catch (final ClientDriverFailedExpectationException bre) {
			Assert.assertEquals("Unexpected request: /blah?gang=goon", bre.getMessage());
		}

	}

}
