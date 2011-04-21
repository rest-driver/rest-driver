package com.github.restdriver.serverdriver.http;

import com.github.restdriver.clientdriver.example.ClientDriverUnitTest;
import com.github.restdriver.serverdriver.http.response.Response;
import com.github.restdriver.types.ClientDriverRequest;
import com.github.restdriver.types.ClientDriverResponse;
import org.junit.Before;
import org.junit.Test;

import static com.github.restdriver.serverdriver.http.RestServerDriver.*;
import static com.github.restdriver.serverdriver.matchers.Matchers.hasStatusCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 13:52
 */
public class PutAcceptanceTest extends ClientDriverUnitTest {


    private String baseUrl;

    @Before
    public void getServerDetails() {
        baseUrl = super.getClientDriver().getBaseUrl();
    }

    @Test
    public void postEmptyBody() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/").withMethod(ClientDriverRequest.Method.POST),
                new ClientDriverResponse("Content"));

        final Response response = post(baseUrl, "");

        assertThat(response, hasStatusCode(200));
        assertThat(response.getContent(), is("Content"));
    }

    @Test
    public void postWithTextPlainBody() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/").withMethod(ClientDriverRequest.Method.POST).withBody("Your body", "text/plain"),
                new ClientDriverResponse("Back at you").withStatus(202));

        final Response response = post(baseUrl, "Your body");

        assertThat(response, hasStatusCode(202));
        assertThat(response.getContent(), is("Back at you"));
    }

    @Test
    public void postWithApplicationXmlBody() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/")
                        .withMethod(ClientDriverRequest.Method.POST)
                        .withBody("<yo/>", "application/xml"),
                new ClientDriverResponse("Back at you").withStatus(202));

        final Response response = post(baseUrl, body("<yo/>", "application/xml"));

        assertThat(response, hasStatusCode(202));
        assertThat(response.getContent(), is("Back at you"));
    }


    @Test
    public void postWithApplicationJsonBodyAndHeaders() {
        getClientDriver().addExpectation(
                new ClientDriverRequest("/jsons")
                        .withMethod(ClientDriverRequest.Method.POST)
                        .withBody("<yo/>", "application/xml"),
                new ClientDriverResponse("Back at you").withStatus(202));

        // TODO: see https://github.com/rest-driver/rest-driver/issues/1
        // we don't know if this test actually sets the headers...

        final Response response = post(baseUrl + "/jsons", body("<yo/>", "application/xml"), header("Accept", "Nothing"));

        assertThat(response, hasStatusCode(202));
        assertThat(response.getContent(), is("Back at you"));
    }


}
