package com.googlecode.rd.serverdriver.http;

import com.googlecode.rd.clientdriver.example.ClientDriverUnitTest;
import com.googlecode.rd.serverdriver.http.response.Response;
import com.googlecode.rd.types.ClientDriverRequest;
import com.googlecode.rd.types.ClientDriverResponse;
import com.googlecode.rd.types.Header;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.rd.serverdriver.http.RestServerDriver.*;
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
        super.getClientDriver().addExpectation(new ClientDriverRequest("/"), new ClientDriverResponse("Content"));

        final Response response = get(baseUrl);

        assertThat(response, hasStatusCode(200));
        assertThat(response.getContent(), is("Content"));
    }

    @Test
    public void getRetrievesHeaders() {
        super.getClientDriver().addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("").withStatus(409).withHeader("X-foo", "barrr"));

        Response response = get(baseUrl);

        assertThat(response, hasStatusCode(409));
        assertThat(response.getHeaders(), hasItem(new Header("X-foo", "barrr")));

    }

    @Test
    public void getIncludesResponseTime() {
        super.getClientDriver().addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("Hello"));

        Response response = get(baseUrl);

        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }

    @Test
    public void getSendsHeaders() {
        super.getClientDriver().addExpectation(
                new ClientDriverRequest("/"),
                new ClientDriverResponse("Hello"));

        // TODO: ClientDriver doesn't match on headers yet,
        // so we don't know if they are actually being sent!

        Response response = get(baseUrl, header("Accept", "Nothing"));

        assertThat(response.getResponseTime(), greaterThanOrEqualTo(0L));
    }


}
