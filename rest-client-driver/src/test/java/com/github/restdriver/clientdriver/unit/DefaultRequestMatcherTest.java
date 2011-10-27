/**
 * Copyright © 2010-2011 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.restdriver.clientdriver.unit;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.DefaultRequestMatcher;

// suppressed to allow inline definition of maps with asMap()
@SuppressWarnings("unchecked")
public class DefaultRequestMatcherTest {

    private DefaultRequestMatcher sut;

    @Before
    public void before() {
        sut = new DefaultRequestMatcher();
    }

    @Test
    public void testMatchNoParams() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testMatchNoParamsPattern() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);
            
        ClientDriverRequest bReq = new ClientDriverRequest(Pattern.compile("[a]{5}")).withMethod(Method.GET);

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testMatchWithParams() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParams(asMap("kk", "vv", "k2", "v2"));
        
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("kk", "vv")
                                        .withParam("k2", "v2");

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testMatchWithParamsPattern() throws IOException {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParams(asMap("kk", "vv", "k2", "v2"));
        
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("kk",Pattern.compile("[v]{2}")).withParam("k2", Pattern.compile("v[0-9]"));

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testMatchWithWrongParam() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "not vv");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithWrongParamPattern() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParams(asMap("kk", "xx"));
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", Pattern.compile("[v]{2}"));

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithNullParam() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParams(asMap("kk", (String) null));
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithParamsTooMany() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParams(asMap("k1", asStringArray("v1")));
        
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("kk", "vv")
                                        .withParam("k2", "v2");
        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithParamsTooFew() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParams(asMap("k1", asStringArray("v1"), "k2", asStringArray("v2")));
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testSuccessfulMatchWithMultipleIdenticalParams() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv").withParam("kk", "vvv");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv").withParam("kk", "vvv");

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testSuccessfulMatchWithMultipleIdenticalParamsInDifferentOrder() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("key", "that").withParam("key", "this");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("key", "this").withParam("key", "that");

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testFailedMatchWithMultipleIdenticalParams() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParams(asMap("kk", asStringArray("vv", "v2")));
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv").withParam("kk", "vvv");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testFailedMatchWithMultipleIdenticalParamsInDifferentOrder() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParams(asMap("key", asStringArray("that", "this")));
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("key", "this").withParam("key", "tha");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testFailedMatchWithWrongNumberOfIdenticalParams() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParams(asMap("key", asStringArray("that")));
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("key", "this").withParam("key", "that");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testFailedMatchWithOneWrongParamPattern() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "v1").withParam("kk", "v2");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", Pattern.compile("[v]{2}"));

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWrongMethod() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.DELETE);

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWrongPath() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);
        ClientDriverRequest bReq = new ClientDriverRequest("bbbbb").withMethod(Method.GET);

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWrongPathPattern() {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);
        ClientDriverRequest bReq = new ClientDriverRequest(Pattern.compile("[b]{5}")).withMethod(Method.GET);

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithRequestBody() throws IOException {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testMatchWithRequestBodyPattern() throws IOException {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody(Pattern.compile("[o]{4}h"), Pattern.compile("text/j[a-z]{3}"));

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testMatchWithRequestBodyWrongType() throws IOException {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/jnkular");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithRequestBodyWrongTypePattern() throws IOException {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/jnkular");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", Pattern.compile("text/[a-z]{4}"));

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithRequestBodyWrongContent() throws IOException {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooook", "texy/junk");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithRequestBodyWrongContentPattern() throws IOException {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooook", "text/junk");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody(Pattern.compile("[o]{4}h"), "text/junk");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithRequestHeaderString() throws Exception {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Cache-Control", "no-cache");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Cache-Control", "no-cache");

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testMatchMultipleWithRequestHeaderString() throws Exception {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);
        aReq.getHeaders().put("Some-Header", Collections.enumeration(Arrays.asList("foo", "bar")));
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Some-Header", "bar");

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testMatchWrongWithRequestHeaderString() throws Exception {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Cache-Control", "cache-please!");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Cache-Control", "no-cache");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWrongWithMissingRequestHeaderString() throws Exception {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Cache-Control", "no-cache");

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWithRequestHeaderPattern() throws Exception {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Content-Length","1234");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Content-Length", Pattern.compile("\\d+"));

        assertThat(sut.isMatch(aReq, bReq), is(true));
    }

    @Test
    public void testMatchWrongWithRequestHeaderPattern() throws Exception {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Content-Length", "invalid");
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Content-Length", Pattern.compile("\\d+"));

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    @Test
    public void testMatchWrongWithMissingRequestHeaderPattern() throws Exception {

        ClientDriverRequest aReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET);
        ClientDriverRequest bReq = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Content-Length", Pattern.compile("\\d+"));

        assertThat(sut.isMatch(aReq, bReq), is(false));
    }

    private String[] asStringArray(String... strings) {
        return strings;
    }

    @SuppressWarnings("rawtypes")
    private static Map asMap(Object... objects) {
        Map map = new HashMap();

        if (objects.length % 2 != 0) {
            throw new RuntimeException("There should be an even number of objects given");
        }

        Object previous = null;

        for (Object object : objects) {
            if (previous == null) {
                previous = object;
            } else {
                map.put(previous, object);
                previous = null;
            }
        }

        return map;
    }
}
