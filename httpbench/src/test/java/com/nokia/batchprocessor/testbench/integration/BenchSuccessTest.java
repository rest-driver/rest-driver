package com.nokia.batchprocessor.testbench.integration;

import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;
import org.junit.Test;

import com.nokia.batchprocessor.testbench.BenchRequest;
import com.nokia.batchprocessor.testbench.BenchResponse;
import com.nokia.batchprocessor.testbench.BenchRequest.Method;
import com.nokia.batchprocessor.testbench.example.HttpUnitTest;

public class BenchSuccessTest extends HttpUnitTest {

    @Test
    public void testJettyWorking200() throws Exception {

        getBenchServer().addExpectation(
                new BenchRequest("/blah"),
                new BenchResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
                        "TestServer"));

        final HttpClient client = new HttpClient();

        final String baseUrl = getBenchServer().getBaseUrl();
        final GetMethod getter = new GetMethod(baseUrl + "/blah");

        client.executeMethod(getter);

        Assert.assertEquals(200, getter.getStatusCode());
        Assert.assertEquals("OUCH!!", getter.getResponseBodyAsString());
        Assert.assertEquals("TestServer", getter.getResponseHeader("Server").getValue());

        getter.releaseConnection();

    }

    @Test
    public void testJettyWorking404() throws Exception {

        final String baseUrl = getBenchServer().getBaseUrl();
        getBenchServer().addExpectation(new BenchRequest("/blah2"), new BenchResponse("o.O").withStatus(404));

        final HttpClient client = new HttpClient();
        final GetMethod getter = new GetMethod(baseUrl + "/blah2");
        client.executeMethod(getter);

        Assert.assertEquals(404, getter.getStatusCode());
        Assert.assertEquals("o.O", getter.getResponseBodyAsString());

        getter.releaseConnection();
    }

    @Test
    public void testJettyWorking500() throws Exception {

        final String baseUrl = getBenchServer().getBaseUrl();
        getBenchServer().addExpectation(new BenchRequest("/blah2"), new BenchResponse("___").withStatus(500));

        final HttpClient client = new HttpClient();
        final GetMethod getter = new GetMethod(baseUrl + "/blah2");
        client.executeMethod(getter);

        Assert.assertEquals(500, getter.getStatusCode());
        Assert.assertEquals("___", getter.getResponseBodyAsString());

        getter.releaseConnection();
    }

    @Test
    public void testJettyWorkingTwoRequests() throws Exception {

        final String baseUrl = getBenchServer().getBaseUrl();
        getBenchServer().addExpectation(new BenchRequest("/blah123"), new BenchResponse("__2_").withStatus(200));
        getBenchServer().addExpectation(new BenchRequest("/blah456"), new BenchResponse("__7_").withStatus(300));

        final HttpClient client = new HttpClient();

        final GetMethod getter1 = new GetMethod(baseUrl + "/blah123");
        client.executeMethod(getter1);
        Assert.assertEquals(200, getter1.getStatusCode());
        Assert.assertEquals("__2_", getter1.getResponseBodyAsString());
        getter1.releaseConnection();

        final GetMethod getter2 = new GetMethod(baseUrl + "/blah456");
        client.executeMethod(getter2);
        Assert.assertEquals(300, getter2.getStatusCode());
        Assert.assertEquals("__7_", getter2.getResponseBodyAsString());
        getter2.releaseConnection();
    }

    @Test
    public void testJettyWorkingWithMethodAndParams() throws Exception {

        getBenchServer().addExpectation(
                new BenchRequest("/blah").withMethod(Method.DELETE).withParam("gang", "green"),
                new BenchResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
                        "TestServer"));

        final HttpClient client = new HttpClient();

        final String baseUrl = getBenchServer().getBaseUrl();
        final DeleteMethod deleter = new DeleteMethod(baseUrl + "/blah?gang=green");

        client.executeMethod(deleter);

        Assert.assertEquals(200, deleter.getStatusCode());
        Assert.assertEquals("OUCH!!", deleter.getResponseBodyAsString());
        Assert.assertEquals("TestServer", deleter.getResponseHeader("Server").getValue());

        deleter.releaseConnection();

    }

    @Test
    public void testJettyWorkingWithMethodAndParamsPattern() throws Exception {

        getBenchServer().addExpectation(
                new BenchRequest(Pattern.compile("/[a-z]l[a-z]{2}")).withMethod(Method.DELETE).withParam("gang",
                        Pattern.compile("gre[a-z]+")),
                new BenchResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
                        "TestServer"));

        final HttpClient client = new HttpClient();

        final String baseUrl = getBenchServer().getBaseUrl();
        final DeleteMethod deleter = new DeleteMethod(baseUrl + "/blah?gang=green");

        client.executeMethod(deleter);

        Assert.assertEquals(200, deleter.getStatusCode());
        Assert.assertEquals("OUCH!!", deleter.getResponseBodyAsString());
        Assert.assertEquals("TestServer", deleter.getResponseHeader("Server").getValue());

        deleter.releaseConnection();

    }

    @Test
    public void testJettyWorkingTwoSameRequests() throws Exception {

        getBenchServer().addExpectation(
                new BenchRequest("/blah"),
                new BenchResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
                        "TestServer"));

        getBenchServer().addExpectation(
                new BenchRequest("/blah"),
                new BenchResponse("OUCH!!404").withStatus(404).withContentType("text/plain404").withHeader("Server",
                        "TestServer404"));

        final HttpClient client = new HttpClient();

        final String baseUrl = getBenchServer().getBaseUrl();

        final GetMethod getter = new GetMethod(baseUrl + "/blah");
        client.executeMethod(getter);

        Assert.assertEquals(200, getter.getStatusCode());
        Assert.assertEquals("OUCH!!", getter.getResponseBodyAsString());
        Assert.assertEquals("TestServer", getter.getResponseHeader("Server").getValue());

        getter.releaseConnection();

        final GetMethod getter2 = new GetMethod(baseUrl + "/blah");
        client.executeMethod(getter2);

        Assert.assertEquals(404, getter2.getStatusCode());
        Assert.assertEquals("OUCH!!404", getter2.getResponseBodyAsString());
        Assert.assertEquals("TestServer404", getter2.getResponseHeader("Server").getValue());

        getter.releaseConnection();

    }

    @Test
    public void testJettyWorkingWithPostBody() throws Exception {

        final String baseUrl = getBenchServer().getBaseUrl();
        getBenchServer().addExpectation(
                new BenchRequest("/blah2").withMethod(Method.PUT).withBody("Jack your body!", "text/plain"),
                new BenchResponse("___").withStatus(501));

        final HttpClient client = new HttpClient();
        final PutMethod putter = new PutMethod(baseUrl + "/blah2");
        putter.setRequestEntity(new StringRequestEntity("Jack your body!", "text/plain", "UTF-8"));
        client.executeMethod(putter);

        Assert.assertEquals(501, putter.getStatusCode());
        Assert.assertEquals("___", putter.getResponseBodyAsString());

        putter.releaseConnection();
    }

    @Test
    public void testJettyWorkingWithPostBodyPattern() throws Exception {

        final String baseUrl = getBenchServer().getBaseUrl();
        getBenchServer().addExpectation(
                new BenchRequest("/blah2").withMethod(Method.PUT).withBody(Pattern.compile("Jack [\\w\\s]+!"),
                        "text/plain"), new BenchResponse("___").withStatus(501));

        final HttpClient client = new HttpClient();
        final PutMethod putter = new PutMethod(baseUrl + "/blah2");
        putter.setRequestEntity(new StringRequestEntity("Jack your body!", "text/plain", "UTF-8"));
        client.executeMethod(putter);

        Assert.assertEquals(501, putter.getStatusCode());
        Assert.assertEquals("___", putter.getResponseBodyAsString());

        putter.releaseConnection();
    }
}
