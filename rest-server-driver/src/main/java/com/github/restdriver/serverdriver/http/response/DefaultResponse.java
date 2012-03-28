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
package com.github.restdriver.serverdriver.http.response;

import static org.apache.commons.lang.StringUtils.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.restdriver.serverdriver.Json;
import com.github.restdriver.serverdriver.Xml;
import com.github.restdriver.serverdriver.http.Header;

/**
 * Our class which describes an HTTP response.
 */
public final class DefaultResponse implements Response {
    
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    private final String protocolVersion;
    private final int statusCode;
    private final String statusMessage;
    private final String content;
    private final List<Header> headers;
    private final long responseTime;
    private final byte[] binaryContent;
    
    /**
     * Constructor from apache HttpResponse.
     * 
     * @param response the HttpResponse
     * @param responseTime time taken for the request in milliseconds
     */
    public DefaultResponse(HttpResponse response, long responseTime) {
        this.protocolVersion = response.getStatusLine().getProtocolVersion().toString();
        this.statusCode = response.getStatusLine().getStatusCode();
        this.statusMessage = response.getStatusLine().getReasonPhrase();
        this.binaryContent = binaryContentFromResponse(response);
        this.content = contentFromResponse(response, this.binaryContent);
        this.headers = headersFromResponse(response);
        this.responseTime = responseTime;
    }
    
    @Override
    public int getStatusCode() {
        return statusCode;
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public String asText() {
        return content;
    }
    
    @Override
    public List<Header> getHeaders() {
        return headers;
    }
    
    @Override
    public List<Header> getHeaders(String headerName) {
        
        List<Header> matchingHeaders = new ArrayList<Header>();
        
        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase(headerName)) {
                matchingHeaders.add(header);
            }
        }
        
        return matchingHeaders;
    }
    
    @Override
    public Header getHeader(String headerName) {
        
        List<Header> matchingHeaders = getHeaders(headerName);
        
        if (matchingHeaders.isEmpty()) {
            return null;
        }
        
        if (matchingHeaders.size() > 1) {
            throw new IllegalStateException("Attempt to get single header '" + headerName + "' but more than one value found.");
        }
        
        return matchingHeaders.get(0);
    }
    
    @Override
    public long getResponseTime() {
        return responseTime;
    }
    
    @Override
    public JsonNode asJson() {
        return Json.asJson(this);
    }
    
    @Override
    public Element asXml() {
        return Xml.asXml(this.getContent());
    }
    
    @Override
    public String toString() {
        return createSummaryString(Response.MAX_BODY_DISPLAY_LENGTH);
    }
    
    @Override
    public String toBigString() {
        return createSummaryString(this.content.length());
    }
    
    private String createSummaryString(int truncateLength) {
        StrBuilder httpString = new StrBuilder();
        httpString.append(protocolVersion).append(" ").append(statusCode).append(" ").append(statusMessage);
        httpString.appendNewLine();
        
        httpString.appendWithSeparators(headers, SystemUtils.LINE_SEPARATOR);
        
        if (StringUtils.isNotEmpty(content)) {
            httpString.appendNewLine();
            httpString.appendNewLine();
            httpString.append(StringUtils.abbreviate(content, truncateLength));
        }
        
        return httpString.toString();
    }
    
    @Override
    public String toCompactString() {
        return "status=" + statusCode + "|content=" + StringUtils.abbreviate(content, Response.MAX_BODY_DISPLAY_LENGTH) + "|headers=[" + join(headers, ",") + "]";
    }
    
    @Override
    public void tinyDump() {
        System.out.println(this.toCompactString());
    }
    
    @Override
    public void dump() {
        System.out.println(this.toString());
    }
    
    @Override
    public void bigDump() {
        System.out.println(this.toBigString());
    }
    
    @Override
    public byte[] asBytes() {
        return binaryContent;
    }
    
    private byte[] binaryContentFromResponse(HttpResponse response) {
        
        InputStream stream = null;
        
        try {
            HttpEntity entity = response.getEntity();
            
            if (entity == null) {
                return null;
            }
            
            stream = entity.getContent();
            return IOUtils.toByteArray(stream);
            
        } catch (IOException e) {
            throw new RuntimeException("Error getting response entity", e);
            
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
    
    private String contentFromResponse(HttpResponse response, byte[] bytes) {
        // we have to take binary content as a param here because we can't read the inputstream twice.
        
        if (bytes == null) {
            return null;
        }
        
        InputStream stream = new ByteArrayInputStream(bytes);
        
        try {
            return readWithEncoding(stream, response.getEntity().getContentEncoding());
        } catch (IOException e) {
            throw new RuntimeException("Error converting response entity to string", e);
        }
        
    }
    
    private List<Header> headersFromResponse(HttpResponse response) {
        List<Header> parsedHeaders = new ArrayList<Header>();
        
        for (org.apache.http.Header currentHeader : response.getAllHeaders()) {
            Header header = new Header(currentHeader.getName(), currentHeader.getValue());
            parsedHeaders.add(header);
        }
        
        return parsedHeaders;
    }
    
    private String readWithEncoding(InputStream stream, org.apache.http.Header contentEncoding) throws IOException {
        if (contentEncoding == null) {
            return IOUtils.toString(stream, DEFAULT_ENCODING);
        } else {
            return IOUtils.toString(stream, contentEncoding.getValue());
        }
    }
    
}
