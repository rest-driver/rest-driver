package com.googlecode.rd.serverdriver.http;

import static com.googlecode.rd.serverdriver.http.HttpAcceptanceTestHelper.*;
import static com.googlecode.rd.serverdriver.json.JsonAcceptanceTestHelper.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.rd.clientdriver.BenchRequest;
import com.googlecode.rd.clientdriver.BenchResponse;
import com.googlecode.rd.clientdriver.example.HttpUnitTest;
import com.googlecode.rd.serverdriver.http.request.GetRequest;
import com.googlecode.rd.serverdriver.http.response.Response;

public class GetAcceptanceTest extends HttpUnitTest {

	private String baseUrl;

	@Before
	public void getServerDetails() {
		baseUrl = super.getBenchServer().getBaseUrl();
	}

	@Test
	public void simpleGetRetrievesStatusAndContent() {

		super.getBenchServer().addExpectation(new BenchRequest("/"), new BenchResponse("Content"));

		final Response response = get(new GetRequest(baseUrl, null));

		assertThat(response, hasStatusCode(200));
		assertThat(response.getContent(), is("Content"));

	}

	@Test
	public void getRetrievesHeaders() {

		super.getBenchServer().addExpectation(
				new BenchRequest("/"),
				new BenchResponse("").withStatus(409).withHeader("X-foo", "barrr"));

		final Response response = get(new GetRequest(baseUrl, null));

		assertThat(response, hasStatusCode(409));
		assertThat(response.getHeaders(), hasItem(new Header("X-foo", "barrr")));

	}

	@Test
	public void getIncludesResponseTime() {

		super.getBenchServer().addExpectation(
				new BenchRequest("/"),
				new BenchResponse("Hello"));

		final Response response = get(new GetRequest(baseUrl, null));

		assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));

	}

	@Test
	public void getWithJsonParser() {

		super.getBenchServer().addExpectation(
				new BenchRequest("/"),
				new BenchResponse("{\"a\":55}").withHeader("Content-Type", "application/json"));

		final Response response = get(new GetRequest(baseUrl, null));

		assertThat(asJson(response), hasJsonValue("a", is(55)));

	}
}
