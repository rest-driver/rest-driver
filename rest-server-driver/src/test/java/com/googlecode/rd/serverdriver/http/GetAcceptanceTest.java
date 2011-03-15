package com.googlecode.rd.serverdriver.http;

import static com.googlecode.rd.serverdriver.http.HttpAcceptanceTestHelper.*;
import static com.googlecode.rd.serverdriver.json.JsonAcceptanceTestHelper.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.rd.clientdriver.example.ClientDriverUnitTest;
import com.googlecode.rd.serverdriver.http.request.GetRequest;
import com.googlecode.rd.serverdriver.http.response.Response;
import com.googlecode.rd.types.ClientDriverRequest;
import com.googlecode.rd.types.ClientDriverResponse;
import com.googlecode.rd.types.Header;

public class GetAcceptanceTest extends ClientDriverUnitTest {

	private String baseUrl;

	@Before
	public void getServerDetails() {
		baseUrl = super.getClientDriver().getBaseUrl();
	}

	@Test
	public void simpleGetRetrievesStatusAndContent() {

		super.getClientDriver().addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse("Content"));

		final Response response = get(new GetRequest(baseUrl, null));

		assertThat(response, hasStatusCode(200));
		assertThat(response.getContent(), is("Content"));

	}

	@Test
	public void getRetrievesHeaders() {

		super.getClientDriver().addExpectation(
				new ClientDriverRequest("/"),
				new ClientDriverResponse("").withStatus(409).withHeader("X-foo", "barrr"));

		final Response response = get(new GetRequest(baseUrl, null));

		assertThat(response, hasStatusCode(409));
		assertThat(response.getHeaders(), hasItem(new Header("X-foo", "barrr")));

	}

	@Test
	public void getIncludesResponseTime() {

		super.getClientDriver().addExpectation(
				new ClientDriverRequest("/"),
				new ClientDriverResponse("Hello"));

		final Response response = get(new GetRequest(baseUrl, null));

		assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));

	}

	@Test
	public void getWithJsonParser() {

		super.getClientDriver().addExpectation(
				new ClientDriverRequest("/"),
				new ClientDriverResponse("{\"a\":55}").withHeader("Content-Type", "application/json"));

		final Response response = get(new GetRequest(baseUrl, null));

		assertThat(asJson(response), hasJsonValue("a", is(55)));

	}
}
