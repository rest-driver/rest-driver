package com.nokia.batchprocessor.testbench.integration;

import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Assert;
import org.junit.Test;

import com.nokia.batchprocessor.testbench.BenchRequest;
import com.nokia.batchprocessor.testbench.BenchResponse;
import com.nokia.batchprocessor.testbench.BenchRuntimeException;
import com.nokia.batchprocessor.testbench.BenchServer;
import com.nokia.batchprocessor.testbench.TestBenchFactory;
import com.nokia.batchprocessor.testbench.BenchRequest.Method;

public class BenchFailTest {

    private BenchServer bServer;

    @Test
    public void testUnexpectedCall() throws Exception {
        bServer = new TestBenchFactory().createBenchServer();

        // No expectations defined

        final HttpClient client = new HttpClient();
        final GetMethod getter = new GetMethod(bServer.getBaseUrl() + "/blah?foo=bar");
        client.executeMethod(getter);
        getter.releaseConnection();

        try {
            bServer.shutdown();
            Assert.fail();
        } catch (final BenchRuntimeException bre) {
            Assert.assertEquals("Unexpected request: /blah?foo=bar", bre.getMessage());
        }

    }

    @Test
    public void testUnmatchedExpectation() throws Exception {
        bServer = new TestBenchFactory().createBenchServer();

        bServer.addExpectation(new BenchRequest("/blah"), new BenchResponse("OUCH!!").withStatus(200));
        bServer.addExpectation(new BenchRequest("/blah"), new BenchResponse("OUCH!!").withStatus(404));

        // no requests made

        try {
            bServer.shutdown();
            Assert.fail();
        } catch (final BenchRuntimeException bre) {
            Assert.assertEquals("2 unmatched expectation(s), first is: BenchRequest: GET /blah; ", bre.getMessage());
        }

    }

    @Test
    public void testJettyWorkingWithMethodButIncorrectParams() throws Exception {
        bServer = new TestBenchFactory().createBenchServer();

        bServer.addExpectation(new BenchRequest("/blah").withMethod(Method.POST).withParam("gang", "green"),
                new BenchResponse("OUCH!!").withStatus(200).withContentType("text/plain").withHeader("Server",
                        "TestServer"));

        final HttpClient client = new HttpClient();

        final String baseUrl = bServer.getBaseUrl();
        final PostMethod poster = new PostMethod(baseUrl + "/blah?gang=groon");

        client.executeMethod(poster);
        poster.releaseConnection();

        try {
            bServer.shutdown();
            Assert.fail();
        } catch (final BenchRuntimeException bre) {
            Assert.assertEquals("Unexpected request: /blah?gang=groon", bre.getMessage());
        }

    }

    @Test
    public void testJettyWorkingWithMethodButIncorrectParamsPattern() throws Exception {
        bServer = new TestBenchFactory().createBenchServer();

        bServer.addExpectation(new BenchRequest(Pattern.compile("/b[a-z]{3}")).withMethod(Method.POST).withParam(
                "gang", Pattern.compile("r")), new BenchResponse("OUCH!!").withStatus(200)
                .withContentType("text/plain").withHeader("Server", "TestServer"));

        final HttpClient client = new HttpClient();

        final String baseUrl = bServer.getBaseUrl();
        final PostMethod poster = new PostMethod(baseUrl + "/blah?gang=goon");

        client.executeMethod(poster);
        poster.releaseConnection();

        try {
            bServer.shutdown();
            Assert.fail();
        } catch (final BenchRuntimeException bre) {
            Assert.assertEquals("Unexpected request: /blah?gang=goon", bre.getMessage());
        }

    }

}
