package com.googlecode.rd.clientdriver.integration;

import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import com.googlecode.rd.clientdriver.example.ClientDriverUnitTest;
import com.googlecode.rd.types.ClientDriverRequest;
import com.googlecode.rd.types.ClientDriverResponse;
import com.googlecode.rd.types.ClientDriverRequest.Method;

public class BenchSuccessTest extends ClientDriverUnitTest {

	@Test
	public void testJettyWorking200() throws Exception {

		getClientDriver().addExpectation(
				new ClientDriverRequest("/blah"),
				new ClientDriverResponse("OUCH!!")
							.withStatus(200)
							.withContentType("text/plain")
							.withHeader("Server", "TestServer"));

		final HttpClient client = new DefaultHttpClient();

		final String baseUrl = getClientDriver().getBaseUrl();
		final HttpGet getter = new HttpGet(baseUrl + "/blah");

		final HttpResponse response = client.execute(getter);

		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		Assert.assertEquals("OUCH!!", IOUtils.toString(response.getEntity().getContent()));
		Assert.assertEquals("TestServer", response.getHeaders("Server")[0].getValue());

	}

	@Test
	public void testJettyWorking404() throws Exception {

		final String baseUrl = getClientDriver().getBaseUrl();
		getClientDriver().addExpectation(new ClientDriverRequest("/blah2"), new ClientDriverResponse("o.O").withStatus(404));

		final HttpClient client = new DefaultHttpClient();
		final HttpGet getter = new HttpGet(baseUrl + "/blah2");
		final HttpResponse response = client.execute(getter);

		Assert.assertEquals(404, response.getStatusLine().getStatusCode());
		Assert.assertEquals("o.O", IOUtils.toString(response.getEntity().getContent()));

	}

	@Test
	public void testJettyWorking500() throws Exception {

		final String baseUrl = getClientDriver().getBaseUrl();
		getClientDriver().addExpectation(new ClientDriverRequest("/blah2"), new ClientDriverResponse("___").withStatus(500));

		final HttpClient client = new DefaultHttpClient();
		final HttpGet getter = new HttpGet(baseUrl + "/blah2");
		final HttpResponse response = client.execute(getter);

		Assert.assertEquals(500, response.getStatusLine().getStatusCode());
		Assert.assertEquals("___", IOUtils.toString(response.getEntity().getContent()));

	}

	@Test
	public void testJettyWorkingTwoRequests() throws Exception {

		final String baseUrl = getClientDriver().getBaseUrl();
		getClientDriver().addExpectation(new ClientDriverRequest("/blah123"), new ClientDriverResponse("__2_").withStatus(200));
		getClientDriver().addExpectation(new ClientDriverRequest("/blah456"), new ClientDriverResponse("__7_").withStatus(300));

		final HttpClient client = new DefaultHttpClient();

		final HttpGet getter1 = new HttpGet(baseUrl + "/blah123");
		final HttpResponse response1 = client.execute(getter1);
		Assert.assertEquals(200, response1.getStatusLine().getStatusCode());
		Assert.assertEquals("__2_", IOUtils.toString(response1.getEntity().getContent()));

		final HttpGet getter2 = new HttpGet(baseUrl + "/blah456");
		final HttpResponse response2 = client.execute(getter2);
		Assert.assertEquals(300, response2.getStatusLine().getStatusCode());
		Assert.assertEquals("__7_", IOUtils.toString(response2.getEntity().getContent()));
	}

	@Test
	public void testJettyWorkingWithMethodAndParams() throws Exception {

		getClientDriver().addExpectation(
				new ClientDriverRequest("/blah").withMethod(Method.DELETE).withParam("gang", "green"),
				new ClientDriverResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
						"TestServer"));

		final HttpClient client = new DefaultHttpClient();

		final String baseUrl = getClientDriver().getBaseUrl();
		final HttpDelete deleter = new HttpDelete(baseUrl + "/blah?gang=green");

		final HttpResponse response = client.execute(deleter);

		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		Assert.assertEquals("OUCH!!", IOUtils.toString(response.getEntity().getContent()));
		Assert.assertEquals("TestServer", response.getHeaders("Server")[0].getValue());

	}

	@Test
	public void testJettyWorkingWithMethodAndParamsPattern() throws Exception {

		getClientDriver().addExpectation(
				new ClientDriverRequest(Pattern.compile("/[a-z]l[a-z]{2}")).withMethod(Method.DELETE).withParam("gang",
						Pattern.compile("gre[a-z]+")),
				new ClientDriverResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
						"TestServer"));

		final HttpClient client = new DefaultHttpClient();

		final String baseUrl = getClientDriver().getBaseUrl();
		final HttpDelete deleter = new HttpDelete(baseUrl + "/blah?gang=green");

		final HttpResponse response = client.execute(deleter);

		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		Assert.assertEquals("OUCH!!", IOUtils.toString(response.getEntity().getContent()));
		Assert.assertEquals("TestServer", response.getHeaders("Server")[0].getValue());

	}

	@Test
	public void testJettyWorkingTwoSameRequests() throws Exception {

		getClientDriver().addExpectation(
				new ClientDriverRequest("/blah"),
				new ClientDriverResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server", "TestServer"));

		getClientDriver().addExpectation(
				new ClientDriverRequest("/blah"),
				new ClientDriverResponse("OUCH!!404").withStatus(404).withContentType("text/plain404").withHeader("Server", "TestServer404"));

		final HttpClient client = new DefaultHttpClient();

		final String baseUrl = getClientDriver().getBaseUrl();

		final HttpGet getter = new HttpGet(baseUrl + "/blah");
		final HttpResponse response = client.execute(getter);

		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		Assert.assertEquals("OUCH!!", IOUtils.toString(response.getEntity().getContent()));
		Assert.assertEquals("TestServer", response.getHeaders("Server")[0].getValue());

		final HttpGet getter2 = new HttpGet(baseUrl + "/blah");
		final HttpResponse response2 = client.execute(getter2);

		Assert.assertEquals(404, response2.getStatusLine().getStatusCode());
		Assert.assertEquals("OUCH!!404", IOUtils.toString(response2.getEntity().getContent()));
		Assert.assertEquals("TestServer404", response2.getHeaders("Server")[0].getValue());

	}

	@Test
	public void testJettyWorkingWithPostBody() throws Exception {

		final String baseUrl = getClientDriver().getBaseUrl();
		getClientDriver().addExpectation(
				new ClientDriverRequest("/blah2").withMethod(Method.PUT).withBody("Jack your body!", "text/plain"),
				new ClientDriverResponse("___").withStatus(501));

		final HttpClient client = new DefaultHttpClient();
		final HttpPut putter = new HttpPut(baseUrl + "/blah2");
		putter.setEntity(new StringEntity("Jack your body!", "text/plain", "UTF-8"));
		final HttpResponse response = client.execute(putter);

		Assert.assertEquals(501, response.getStatusLine().getStatusCode());
		Assert.assertEquals("___", IOUtils.toString(response.getEntity().getContent()));

	}

	@Test
	public void testJettyWorkingWithPostBodyPattern() throws Exception {

		final String baseUrl = getClientDriver().getBaseUrl();
		getClientDriver().addExpectation(
				new ClientDriverRequest("/blah2").withMethod(Method.PUT).withBody(Pattern.compile("Jack [\\w\\s]+!"),
						"text/plain"), new ClientDriverResponse("___").withStatus(501));

		final HttpClient client = new DefaultHttpClient();
		final HttpPut putter = new HttpPut(baseUrl + "/blah2");
		putter.setEntity(new StringEntity("Jack your body!", "text/plain", "UTF-8"));
		final HttpResponse response = client.execute(putter);

		Assert.assertEquals(501, response.getStatusLine().getStatusCode());
		Assert.assertEquals("___", IOUtils.toString(response.getEntity().getContent()));
	}
}
