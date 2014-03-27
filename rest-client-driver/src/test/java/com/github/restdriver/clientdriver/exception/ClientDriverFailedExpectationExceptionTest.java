/**
 * Copyright �� 2010-2011 Nokia
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
package com.github.restdriver.clientdriver.exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import com.github.restdriver.clientdriver.ClientDriverExpectation;
import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverRequestResponsePair;
import com.github.restdriver.clientdriver.HttpRealRequest;
import com.github.restdriver.clientdriver.unit.DummyServletInputStream;
import org.junit.Test;

import static java.util.Collections.enumeration;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientDriverFailedExpectationExceptionTest {
    @Test
    public void should_include_failed_expectations_in_output() throws Exception {
        try {
            throw new ClientDriverFailedExpectationException(createExpectations());
        } catch (ClientDriverFailedExpectationException e) {
            assertThat(e.getMessage(), containsString("PUT"));
            assertThat(e.getMessage(), containsString("/ok"));
            assertThat(e.getMessage(), containsString("/fail"));
            assertThat(e.getMessage(), containsString("containing \"p_test\""));
            assertThat(e.getMessage(), containsString("ending with \"h_test\""));
            assertThat(e.getMessage(), containsString("header-not-here"));
            assertThat(e.getMessage(), containsString("content_type"));
            assertThat(e.getMessage(), containsString("containing \"body\""));
        }
    }

    @Test
    public void should_include_unexpected_requests_and_expectations_in_output() throws Exception {
        try {
            throw new ClientDriverFailedExpectationException(createUnexpectedRequests(), createExpectations());
        } catch (ClientDriverFailedExpectationException e) {
            assertThat(e.getMessage(), containsString("/ok;"));
            assertThat(e.getMessage(), containsString("/fail;"));
            assertThat(e.getMessage(), containsString("p1=[test1]"));
            assertThat(e.getMessage(), containsString("header1: h_val1"));
            assertThat(e.getMessage(), containsString("TYPE app"));
            assertThat(e.getMessage(), containsString("TYPE app"));

            assertThat(e.getMessage(), containsString("/ok\";"));
            assertThat(e.getMessage(), containsString("/fail\";"));
        }
    }

    private List<ClientDriverExpectation> createExpectations() {
        return newArrayList(
            createExpectation("/ok"),
            createExpectation("/fail")
        );
    }

    private ClientDriverExpectation createExpectation(String path) {
        return new ClientDriverExpectation(createPair(path));
    }

    private ClientDriverRequestResponsePair createPair(String path) {
        return new ClientDriverRequestResponsePair(createRequest(path), null);
    }

    private ClientDriverRequest createRequest(String path) {
        return new ClientDriverRequest(path)
            .withMethod(Method.PUT)
            .withHeader("header", endsWith("h_test"))
            .withoutHeader("header-not-here")
            .withParam("param", containsString("p_test"))
            .withBody(containsString("body"), "content_type");
    }

    private List<HttpRealRequest> createUnexpectedRequests() {
        return newArrayList(
            createUnexpectedRequest("/ok"),
            createUnexpectedRequest("/fail")
        );
    }

    private HttpRealRequest createUnexpectedRequest(String path) {
        HttpServletRequest mock = mock(HttpServletRequest.class);

        when(mock.getPathInfo()).thenReturn(path);
        when(mock.getMethod()).thenReturn("POST");
        when(mock.getQueryString()).thenReturn("p1=test1&p2=test2");
        when(mock.getHeaderNames()).thenReturn(createHeaderNames());
        when(mock.getHeader(anyString())).thenReturn("h_val1", "h_val2");
        when(mock.getContentType()).thenReturn("application/text");

        try {
            when(mock.getInputStream()).thenReturn(createInputStream());
        } catch (IOException e) {
            // Should never happen
        }

        return new HttpRealRequest(mock);
    }

    private Enumeration<String> createHeaderNames() {
        return enumeration(newArrayList("header1", "header2"));
    }

    private ServletInputStream createInputStream() {
        return new DummyServletInputStream(new ByteArrayInputStream("test".getBytes()));
    }
}
