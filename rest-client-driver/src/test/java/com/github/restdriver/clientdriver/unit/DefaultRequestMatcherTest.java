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
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.DefaultRequestMatcher;
import com.github.restdriver.clientdriver.RealRequest;

// suppressed to allow inline definition of maps with asMap()
@SuppressWarnings("unchecked")
public class DefaultRequestMatcherTest {

    private Map<String, Object> headers;
    private Map<String, Collection<String>> params;
    private String content;
    private String contentType;
    
    private DefaultRequestMatcher sut;

    @Before
    public void before() {
        headers = new HashMap<String, Object>();
        params = new HashMap<String, Collection<String>>();
        content = null;
        contentType = null;
        sut = new DefaultRequestMatcher();
    }
    
    private RealRequest mockRealRequest(String path, Method method, Map<String, Object> headers,
            Map<String, Collection<String>> params, String content, String contentType) {
        RealRequest real = mock(RealRequest.class);
        when(real.getPath()).thenReturn(path);
        when(real.getMethod()).thenReturn(method);
        when(real.getHeaders()).thenReturn(headers);
        when(real.getParams()).thenReturn(params);
        when(real.getBodyContent()).thenReturn(content);
        when(real.getBodyContentType()).thenReturn(contentType);
        return real;
    }

    @Test
    public void testMatchNoParams() {
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET);

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testMatchNoParamsPattern() {

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest(Pattern.compile("[a]{5}")).withMethod(Method.GET);

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testMatchWithParams() {
        
        params = asMap("kk", "vv", "k2", "v2");

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("kk", "vv")
                                        .withParam("k2", "v2");

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testMatchWithParamsPattern() throws IOException {
        
        params = asMap("kk", "vv", "k2", "v2");

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("kk",Pattern.compile("[v]{2}")).withParam("k2", Pattern.compile("v[0-9]"));

        assertThat(sut.isMatch(real, expected), is(true));
    }
    
    @Test
    public void testMatchWithIntegerParam() throws IOException {
        
        params = asMap("number", "10");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("number", 10);
        
        assertThat(sut.isMatch(real, expected), is(true));
        
    }
    
    @Test
    public void testMatchWithLongParam() throws IOException {
        
        params = asMap("number", "10");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("number", 10L);
        
        assertThat(sut.isMatch(real, expected), is(true));
        
    }
    
    @Test
    public void testMatchWithBooleanParam() throws IOException {
        
        params = asMap("number", "true");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("number", true);
        
        assertThat(sut.isMatch(real, expected), is(true));
        
    }
    
    @Test
    public void testMatchWithObjectParam() throws IOException {
        
        Object param = Method.POST;
        
        params = asMap("number", "POST");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("number", param);
        
        assertThat(sut.isMatch(real, expected), is(true));
        
    }

    @Test
    public void testMatchWithWrongParam() {

        params = asMap("kk", "not vv");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithWrongParamPattern() {
        
        params = asMap("kk", "xx");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", Pattern.compile("[v]{2}"));

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithNullParam() {
        
        params = asMap("kk", (String) null);

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithParamsTooMany() {
        
        params = asMap("k1", "v1");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa")
                                        .withMethod(Method.GET)
                                        .withParam("kk", "vv")
                                        .withParam("k2", "v2");
        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithParamsTooFew() {
        
        params = asMap("k1", "v1", "k2", "v2");

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testSuccessfulMatchWithMultipleIdenticalParams() {
        
        params = asMap("kk", asStringList("vv", "vvv"));

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv").withParam("kk", "vvv");

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testSuccessfulMatchWithMultipleIdenticalParamsInDifferentOrder() {
        
        params = asMap("key", asStringList("that", "this"));

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("key", "this").withParam("key", "that");

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testFailedMatchWithMultipleIdenticalParams() {
        
        params = asMap("kk", asStringList("vv", "v2"));

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", "vv").withParam("kk", "vvv");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testFailedMatchWithMultipleIdenticalParamsInDifferentOrder() {
        
        params = asMap("key", asStringList("that", "this"));

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("key", "this").withParam("key", "tha");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testFailedMatchWithWrongNumberOfIdenticalParams() {
        
        params = asMap("key", "that");

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("key", "this").withParam("key", "that");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testFailedMatchWithOneWrongParamPattern() {
        
        params = asMap("kk", asStringList("v1", "v2"));

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withParam("kk", Pattern.compile("[v]{2}"));

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWrongMethod() {
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.DELETE);

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWrongPath() {

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("bbbbb").withMethod(Method.GET);

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWrongPathPattern() {

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest(Pattern.compile("[b]{5}")).withMethod(Method.GET);

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithRequestBody() throws IOException {
        
        content = "ooooh";
        contentType = "text/junk";
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testMatchWithRequestBodyPattern() throws IOException {
        
        content = "ooooh";
        contentType = "text/junk";
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody(Pattern.compile("[o]{4}h"), Pattern.compile("text/j[a-z]{3}"));

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testMatchWithRequestBodyWrongType() throws IOException {
        
        content = "ooooh";
        contentType = "text/jnkular";
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithRequestBodyWrongTypePattern() throws IOException {
        
        content = "ooooh";
        contentType = "text/jnkular";
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", Pattern.compile("text/[a-z]{4}"));

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithRequestBodyWrongContent() throws IOException {
        
        content = "ooook";
        contentType = "texy/junk";
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody("ooooh", "text/junk");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithRequestBodyWrongContentPattern() throws IOException {
        
        content = "ooook";
        contentType = "text/junk";
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withBody(Pattern.compile("[o]{4}h"), "text/junk");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithRequestHeaderString() throws Exception {
        
        headers.put("Cache-Control", "no-cache");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Cache-Control", "no-cache");

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testMatchMultipleWithRequestHeaderString() throws Exception {
        
        headers.put("Some-Header", Collections.enumeration(Arrays.asList("foo", "bar")));
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Some-Header", "bar");

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testMatchWrongWithRequestHeaderString() throws Exception {
        
        headers.put("Cache-Control", "cache-please!");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Cache-Control", "no-cache");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWrongWithMissingRequestHeaderString() throws Exception {

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Cache-Control", "no-cache");

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWithRequestHeaderPattern() throws Exception {
        
        headers.put("Content-Length", "1234");

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Content-Length", Pattern.compile("\\d+"));

        assertThat(sut.isMatch(real, expected), is(true));
    }

    @Test
    public void testMatchWrongWithRequestHeaderPattern() throws Exception {
        
        headers.put("Content-Length", "invalid");
        
        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Content-Length", Pattern.compile("\\d+"));

        assertThat(sut.isMatch(real, expected), is(false));
    }

    @Test
    public void testMatchWrongWithMissingRequestHeaderPattern() throws Exception {

        RealRequest real = mockRealRequest("aaaaa", Method.GET, headers, params, content, contentType);
        ClientDriverRequest expected = new ClientDriverRequest("aaaaa").withMethod(Method.GET).withHeader("Content-Length", Pattern.compile("\\d+"));

        assertThat(sut.isMatch(real, expected), is(false));
    }

    private static List<String> asStringList(String... strings) {
        return Arrays.asList(strings);
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
                if (object instanceof String) {
                    map.put(previous, asStringList((String) object));
                } else {
                    map.put(previous, object);
                }
                previous = null;
            }
        }

        return map;
    }
}
