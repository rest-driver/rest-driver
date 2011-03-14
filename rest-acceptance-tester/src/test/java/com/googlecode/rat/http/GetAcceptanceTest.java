package com.googlecode.rat.http;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.rat.http.request.GetRequest;
import com.googlecode.rat.http.response.Response;
import com.nokia.batchprocessor.testbench.BenchRequest;
import com.nokia.batchprocessor.testbench.BenchResponse;
import com.nokia.batchprocessor.testbench.example.HttpUnitTest;

import static com.googlecode.rat.http.HttpAcceptanceTestHelper.*;

import static org.hamcrest.Matchers.*;

import static org.hamcrest.MatcherAssert.*;

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
}
