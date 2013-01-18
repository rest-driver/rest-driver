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
package com.github.restdriver.serverdriver.http;

import com.github.restdriver.exception.RuntimeXmlParseException;
import com.github.restdriver.serverdriver.http.exception.RuntimeMappingException;
import com.github.restdriver.serverdriver.http.response.DefaultResponse;
import com.github.restdriver.serverdriver.http.response.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.*;
import org.apache.http.message.BasicHeader;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.github.restdriver.serverdriver.RestServerDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class DefaultResponseTest {

    private void setMockStatusCode(HttpResponse mockResponse, int code) {
        StatusLine mockStatusLine = mock(StatusLine.class);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(code);
        when(mockStatusLine.getProtocolVersion()).thenReturn(new ProtocolVersion("HTTP", 1, 1));
        when(mockStatusLine.getReasonPhrase()).thenReturn("Reason");
    }

    @Test
    public void statusCodeIsTakenFromApacheClass() {
        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 456);
        when(mockResponse.getAllHeaders()).thenReturn(new org.apache.http.Header[0]);

        Response response = new DefaultResponse(mockResponse, 12345);

        assertThat(response.getStatusCode(), is(456));
    }

    @Test
    public void responseTimeIsReportedCorrectly() {
        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 456);
        when(mockResponse.getAllHeaders()).thenReturn(new org.apache.http.Header[0]);

        Response response = new DefaultResponse(mockResponse, 12345);

        assertThat(response.getResponseTime(), is(12345L));
    }

    @Test
    public void singleHeaderIsTakenFromApacheClass() {
        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 456);

        Header[] mockedHeaders = new Header[]{new BasicHeader("headerA", "valueA")};

        when(mockResponse.getAllHeaders()).thenReturn(mockedHeaders);

        Response response = new DefaultResponse(mockResponse, 12345);

        assertThat(response.getHeaders(), Matchers.<Object>hasSize(1));
        assertThat(response.getHeaders().get(0), equalTo(header("headerA", "valueA")));
    }

    @Test
    public void getHeaderByNameReturnsNullIfNoSuchHeader() {
        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 456);

        Header[] mockedHeaders = new Header[]{};

        when(mockResponse.getAllHeaders()).thenReturn(mockedHeaders);

        Response response = new DefaultResponse(mockResponse, 12345);

        assertThat(response.getHeader("myheader"), nullValue());
        assertThat(response.getHeaders("myheader").size(), is(0));
    }

    @Test
    public void getHeaderByNameReturnsTheHeaderIfThereIsOne() {
        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 456);

        Header[] mockedHeaders = new Header[]{new BasicHeader("myheader", "myValue")};

        when(mockResponse.getAllHeaders()).thenReturn(mockedHeaders);

        Response response = new DefaultResponse(mockResponse, 12345);

        com.github.restdriver.serverdriver.http.Header expectedHeader =
                new com.github.restdriver.serverdriver.http.Header("myheader", "myValue");

        assertThat(response.getHeader("myheader"), equalTo(expectedHeader));
        assertThat(response.getHeaders("myheader").size(), equalTo(1));
    }

    @Test(expected = IllegalStateException.class)
    public void getHeaderByNameThrowsExceptionIfThereIsMoreThanOne() {
        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 456);

        Header[] mockedHeaders = new Header[]{
                new BasicHeader("myheader", "myValue"),
                new BasicHeader("myheader", "myOtherValue")
        };

        when(mockResponse.getAllHeaders()).thenReturn(mockedHeaders);

        // throws exception
        new DefaultResponse(mockResponse, 12345).getHeader("myheader");
    }

    @Test
    public void getHeadersByNameReturnsAListIfThereIsMoreThanOne() {
        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 456);

        Header[] mockedHeaders = new Header[]{
                new BasicHeader("myheader", "myValue"),
                new BasicHeader("myheader", "myOtherValue")
        };

        when(mockResponse.getAllHeaders()).thenReturn(mockedHeaders);

        Response response = new DefaultResponse(mockResponse, 12345);

        assertThat(response.getHeaders("myheader").size(), is(2));
    }

    @Test
    public void getHeaderByNameIsCaseInsensitive() {
        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 456);

        Header[] mockedHeaders = new Header[]{new BasicHeader("MYHEADER", "myValue")};

        when(mockResponse.getAllHeaders()).thenReturn(mockedHeaders);

        Response response = new DefaultResponse(mockResponse, 12345);

        com.github.restdriver.serverdriver.http.Header expectedHeader =
                new com.github.restdriver.serverdriver.http.Header("MYHEADER", "myValue");

        assertThat(response.getHeader("myheader"), equalTo(expectedHeader));
        assertThat(response.getHeaders("myheader").size(), equalTo(1));
    }

    @Test
    public void noContentEncodingOnResponseIsTreatedAsUTF8() throws Exception {
        HttpEntity mockEntity = mock(HttpEntity.class);
        when(mockEntity.getContentEncoding()).thenReturn(null);
        when(mockEntity.getContent()).thenReturn(IOUtils.toInputStream("こんにちは", "UTF-8"));

        Header[] mockedHeaders = new Header[0];

        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 200);
        when(mockResponse.getAllHeaders()).thenReturn(mockedHeaders);
        when(mockResponse.getEntity()).thenReturn(mockEntity);

        Response response = new DefaultResponse(mockResponse, 12345);

        assertThat(response.getContent(), is("こんにちは"));
    }

    @Test
    public void contentEncodingOnResponseIsReadUsingThatEncoding() throws Exception {
        Header mockContentEncodingHeader = mock(Header.class);
        when(mockContentEncodingHeader.getValue()).thenReturn("ISO-8859-1");

        HttpEntity mockEntity = mock(HttpEntity.class);
        when(mockEntity.getContentEncoding()).thenReturn(mockContentEncodingHeader);
        when(mockEntity.getContent()).thenReturn(IOUtils.toInputStream("こんにちは"));
        HttpResponse mockResponse = createMockResponse(mockEntity);

        Response response = new DefaultResponse(mockResponse, 12345);

        assertThat(response.getContent(), is(not("こんにちは")));
    }

    @Test
    public void asBytesReturnsCorrectByteArray() throws Exception {

        byte[] bytes = new byte[]{1, 2, 3, 4};

        HttpEntity mockEntity = mock(HttpEntity.class);
        when(mockEntity.getContent()).thenReturn(new ByteArrayInputStream(bytes));
        HttpResponse mockResponse = createMockResponse(mockEntity);

        Response response = new DefaultResponse(mockResponse, 12345);

        assertThat(response.asBytes(), is(bytes));
    }

    @Test
    public void stringAndBytesWorkTogether() throws Exception {

        byte[] bytes = new byte[]{74, 101, 102, 102};

        HttpEntity mockEntity = mock(HttpEntity.class);
        when(mockEntity.getContent()).thenReturn(new ByteArrayInputStream(bytes));
        HttpResponse mockResponse = createMockResponse(mockEntity);

        Response response = new DefaultResponse(mockResponse, 12345);

        assertThat(response.asBytes(), is(bytes));
        assertThat(response.asText(), is("Jeff"));
    }

    @Test
    public void asJsonErrorGivesClearMessage() throws IOException {

        HttpEntity mockEntity = mock(HttpEntity.class);
        when(mockEntity.getContent()).thenReturn(IOUtils.toInputStream("This is not really json, is it?", "utf-8"));
        HttpResponse mockResponse = createMockResponse(mockEntity);
        Response response = new DefaultResponse(mockResponse, 12345);

        try {
            response.asJson();
            Assert.fail();

        } catch (RuntimeMappingException rme) {
            assertThat(rme.getMessage(), is("Can't parse JSON.  Bad content >> This is not real..."));

        }

    }

    @Test
    public void asXmlErrorGivesClearMessage() throws IOException {

        HttpEntity mockEntity = mock(HttpEntity.class);
        when(mockEntity.getContent()).thenReturn(IOUtils.toInputStream("This is not really xml, is it?", "utf-8"));
        HttpResponse mockResponse = createMockResponse(mockEntity);
        Response response = new DefaultResponse(mockResponse, 12345);

        try {
            response.asXml();
            Assert.fail();

        } catch (RuntimeXmlParseException rxpe) {
            assertThat(rxpe.getMessage(), is("Can't parse XML.  Bad content >> This is not real..."));

        }

    }

    private HttpResponse createMockResponse(HttpEntity mockEntity) {
        HttpResponse mockResponse = mock(HttpResponse.class);
        setMockStatusCode(mockResponse, 200);
        when(mockResponse.getAllHeaders()).thenReturn(new Header[0]);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        return mockResponse;
    }

}
