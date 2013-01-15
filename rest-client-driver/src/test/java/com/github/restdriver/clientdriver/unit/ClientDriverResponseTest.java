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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.exception.ClientDriverResponseCreationException;

public class ClientDriverResponseTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void creatingResponseWithoutContentGives204Status() {
        ClientDriverResponse response = new ClientDriverResponse();
        
        assertThat(response.getStatus(), is(204));
    }
    
    @Test
    public void creatingResponseWithStringContentGives200Status() {
        ClientDriverResponse response = new ClientDriverResponse("content");
        
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void creatingResponseWithInputStreamContentGives200Status() {
        ClientDriverResponse response = new ClientDriverResponse(IOUtils.toInputStream("content"));
        
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void creatingResponseWithEmptyStringContentGives200Status() {
        ClientDriverResponse response = new ClientDriverResponse("");
        
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void creatingResponseWithEmptyInputStreamGives200Status() {
        ClientDriverResponse response = new ClientDriverResponse(IOUtils.toInputStream(""));
        
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void creatingResponseWithNullStringGives204Status() {
        ClientDriverResponse response = new ClientDriverResponse((String) null);
        
        assertThat(response.getStatus(), is(204));
    }
    
    @Test
    public void creatingResponseWithNullInputStreamGives204Status() {
        ClientDriverResponse response = new ClientDriverResponse((InputStream) null);
        
        assertThat(response.getStatus(), is(204));
    }
    
    @Test
    public void creatingResponseWithStringContentHasTextContentType() {
        ClientDriverResponse response = new ClientDriverResponse("content");
        
        assertThat(response.getContentType(), is("text/plain"));
    }
    
    @Test
    public void creatingResponseWithInputStreamContentHasBinaryContentType() {
        ClientDriverResponse response = new ClientDriverResponse(IOUtils.toInputStream("content"));
        
        assertThat(response.getContentType(), is("application/octet-stream"));
    }
    
    @Test
    public void creatingEmptyResponseGivesNoContentType() {
        
        assertThat(new ClientDriverResponse().getContentType(), is(nullValue()));
        assertThat(new ClientDriverResponse((String) null).getContentType(), is(nullValue()));
        assertThat(new ClientDriverResponse("").getContentType(), is(nullValue()));
        assertThat(new ClientDriverResponse((InputStream) null).getContentType(), is(nullValue()));
        assertThat(new ClientDriverResponse(IOUtils.toInputStream("")).getContentType(), is(nullValue()));
        
    }
    
    @Test
    public void creatingResponseWithStringContentHasBody() {
        ClientDriverResponse response = new ClientDriverResponse("content");
        
        assertThat(response.hasBody(), is(true));
    }
    
    @Test
    public void creatingResponseWithInputStreamContentHasBody() {
        ClientDriverResponse response = new ClientDriverResponse(IOUtils.toInputStream("content"));
        
        assertThat(response.hasBody(), is(true));
    }
    
    @Test
    public void creatingEmptyResponseHasNoBody() {
        
        assertThat(new ClientDriverResponse().hasBody(), is(false));
        assertThat(new ClientDriverResponse((String) null).hasBody(), is(false));
        assertThat(new ClientDriverResponse("").hasBody(), is(false));
        assertThat(new ClientDriverResponse((InputStream) null).hasBody(), is(false));
        assertThat(new ClientDriverResponse(IOUtils.toInputStream("")).hasBody(), is(false));
        
    }
    
    @Test
    public void creatingResponseWithStringReturnsCorrectValueWhenFetchingContentAsString() {
        ClientDriverResponse response = new ClientDriverResponse("some text");
        
        assertThat(response.getContent(), is("some text"));
    }
    
    @Test
    public void creatingResponseWithInputStreamReturnsCorrectValueWhenFetchingContentAsString() {
        ClientDriverResponse response = new ClientDriverResponse(IOUtils.toInputStream("some text"));
        
        assertThat(response.getContent(), is("some text"));
    }
    
    @Test
    public void creatingEmptyResponseHasEmptyStringContentWhenFetchingContentAsString() {
        
        assertThat(new ClientDriverResponse().getContent(), is(""));
        assertThat(new ClientDriverResponse((String) null).getContent(), is(""));
        assertThat(new ClientDriverResponse("").getContent(), is(""));
        assertThat(new ClientDriverResponse((InputStream) null).getContent(), is(""));
        assertThat(new ClientDriverResponse(IOUtils.toInputStream("")).getContent(), is(""));
        
    }
    
    @Test
    public void creatingRepsonseWithStringReturnsCorrectByteArrayWhenFetchingContent() {
        ClientDriverResponse response = new ClientDriverResponse("some text");
        
        assertThat(response.getContentAsBytes(), is(("some text").getBytes()));
    }
    
    @Test
    public void creatingResponseWithInputStreamReturnsCorrectByteArrayWhenFetchingContent() {
        ClientDriverResponse response = new ClientDriverResponse(IOUtils.toInputStream("some text"));
        
        assertThat(response.getContentAsBytes(), is(("some text").getBytes()));
    }
    
    @Test
    public void creatingEmptyResponseHasNullByteArrayWhenFetchingContent() {
        
        assertThat(new ClientDriverResponse().getContentAsBytes(), is(nullValue()));
        assertThat(new ClientDriverResponse((String) null).getContentAsBytes(), is(nullValue()));
        assertThat(new ClientDriverResponse("").getContentAsBytes(), is(nullValue()));
        assertThat(new ClientDriverResponse((InputStream) null).getContentAsBytes(), is(nullValue()));
        assertThat(new ClientDriverResponse(IOUtils.toInputStream("")).getContentAsBytes(), is(nullValue()));
        
    }
    
    @Test
    public void creatingResponseWithTroublesomeInputStreamThrowsClientResponseCreationException() throws IOException {
        
        thrown.expect(ClientDriverResponseCreationException.class);
        thrown.expectMessage("unable to create client driver response");
        
        InputStream mockInputStream = mock(InputStream.class);
        when(mockInputStream.read((byte[]) anyObject())).thenThrow(new IOException("exception reading stream"));
        
        new ClientDriverResponse(mockInputStream);
    }
    
    @Test
    public void usingHeaderCanOverrideContentType() {
        ClientDriverResponse response = new ClientDriverResponse("hello").withContentType("text/plain");
        
        assertThat(response.getContentType(), is("text/plain"));
        
        response.withHeader("Content-Type", "text/xml");
        
        assertThat(response.getContentType(), is("text/xml"));
    }
    
    @Test
    public void usingHeaderCanOverrideContentTypeIgnoringCase() {
        ClientDriverResponse response = new ClientDriverResponse("hello").withContentType("text/plain");
        
        assertThat(response.getContentType(), is("text/plain"));
        
        response.withHeader("content-type", "text/xml");
        
        assertThat(response.getContentType(), is("text/xml"));
    }
    
    @Test
    public void usingStatusCodeOverridesDefaultStatusCode() {
        ClientDriverResponse response = new ClientDriverResponse().withStatus(201);
        
        assertThat(response.getStatus(), is(201));
    }
    
    @Test
    public void customHeadersCanBeSet() {
        ClientDriverResponse response = new ClientDriverResponse();
        
        response.withHeader("Server", "server-name");
        
        assertThat(response.getHeaders(), hasEntry("Server", "server-name"));
    }
    
}
