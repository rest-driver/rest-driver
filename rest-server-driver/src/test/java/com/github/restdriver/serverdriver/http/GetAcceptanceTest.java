package com.github.restdriver.serverdriver.http;

import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.example.ClientDriverUnitTest;
import com.github.restdriver.serverdriver.http.response.Response;
import com.github.restdriver.clientdriver.ClientDriverRequest;
import org.junit.Before;
import org.junit.Test;

import static com.github.restdriver.serverdriver.http.RestServerDriver.*;
import static com.github.restdriver.serverdriver.matchers.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetAcceptanceTest extends ClientDriverUnitTest {

    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = super.getClientDriver().getBaseUrl();
    }

    @Test
    public void simpleGetRetrievesStatusAndContent() {
        getClientDriver().addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse("Content"));

        Response response = get(baseUrl);

        assertThat(response, hasStatusCode(200));
        assertThat(response.getContent(), is("Content"));
    }

    @Test
    public void getRetrievesHeaders() {
        getClientDriver().addExpectation(
            new ClientDriverRequest("/"),
            new ClientDriverResponse("").withStatus(409).withHeader("X-foo", "barrr"));

        Response response = get(baseUrl);

        assertThat(response, hasStatusCode(409));
        assertThat(response.getHeaders(), hasItem(new Header("X-foo", "barrr")));

    }

    @Test
    public void getIncludesResponseTime() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("Hello"));

        Response response = get(baseUrl);

        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }

    @Test
    public void getSendsHeaders() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("Hello"));

        // TODO: ClientDriver doesn't match on headers yet,
        // so we don't know if they are actually being sent!

        Response response = get(baseUrl, header("Accept", "Nothing"));

        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }


}
