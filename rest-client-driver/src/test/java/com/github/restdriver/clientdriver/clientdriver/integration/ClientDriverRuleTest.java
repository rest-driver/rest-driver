package com.github.restdriver.clientdriver.clientdriver.integration;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

public class ClientDriverRuleTest {

    @Rule
    public ClientDriverRule driver = ClientDriverRule.none();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void letsTrySomethingThatFails() throws Exception {

        // We use ExpectedException to catch the exception we (hopefully) get because the expectations weren't met
        thrown.expect(ClientDriverFailedExpectationException.class);

        driver.expect(new ClientDriverRequest("/blah"), new ClientDriverResponse("OUCH!!").withStatus(200));
        driver.expect(new ClientDriverRequest("/blah"), new ClientDriverResponse("OUCH!!").withStatus(404));

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(driver.getBaseUrl() + "/blah?gang=groon");

        client.execute(post);

    }

    @Test
    public void letsTrySomethingThatWorks() throws Exception {

        driver.expect(new ClientDriverRequest("/blah"), new ClientDriverResponse("").withStatus(404));

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(driver.getBaseUrl() + "/blah");

        client.execute(get);

    }

}
