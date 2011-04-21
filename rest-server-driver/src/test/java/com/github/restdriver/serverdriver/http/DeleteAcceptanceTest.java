package com.github.restdriver.serverdriver.http;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.example.ClientDriverUnitTest;
import com.github.restdriver.serverdriver.http.response.Response;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import org.junit.Before;
import org.junit.Test;

import static com.github.restdriver.serverdriver.RestServerDriver.delete;
import static com.github.restdriver.serverdriver.RestServerDriver.header;
import static com.github.restdriver.serverdriver.Matchers.hasStatusCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DeleteAcceptanceTest extends ClientDriverUnitTest {

    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = super.getClientDriver().getBaseUrl();
    }

    @Test
    public void simpleDeleteRetrievesStatusAndContent() {

        getClientDriver().addExpectation(
                new ClientDriverRequest("/").withMethod(ClientDriverRequest.Method.DELETE),
                new ClientDriverResponse("Content"));

        Response response = delete(baseUrl);

        assertThat(response, hasStatusCode(200));
        assertThat(response.getContent(), is("Content"));
    }

    @Test
    public void deleteSendsHeaders() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/").withMethod(ClientDriverRequest.Method.DELETE),
                new ClientDriverResponse("Hello"));

        // TODO: ClientDriver doesn't match on headers yet,
        // so we don't know if they are actually being sent!

        Response response = delete(baseUrl, header("Accept", "Nothing"));
        assertThat(response.getContent(), is("Hello"));
    }


}
