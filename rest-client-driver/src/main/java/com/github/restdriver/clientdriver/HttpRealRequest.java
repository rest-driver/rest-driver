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
package com.github.restdriver.clientdriver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpRealRequest implements RealRequest {
    
    private final Method method;
    private final String path;
    private final Multimap<String, String> params;
    private final Map<String, Object> headers;
    private final byte[] bodyContent;
    private final String bodyContentType;
    
    public HttpRealRequest(HttpServletRequest request) {
        this.path = request.getPathInfo();
        this.method = Method.custom(request.getMethod().toUpperCase());
        this.params = HashMultimap.create();
        
        if (request.getQueryString() != null) {
            MultiMap<String> parameterMap = new MultiMap<String>();
            UrlEncoded.decodeTo(request.getQueryString(), parameterMap, UTF_8);
            for (Entry<String, String[]> paramEntry : parameterMap.toStringArrayMap().entrySet()) {
                String[] values = paramEntry.getValue();
                for (String value : values) {
                    this.params.put(paramEntry.getKey(), value);
                }
            }
        }
        
        headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName.toLowerCase(), request.getHeader(headerName));
            }
        }
        
        try {
            this.bodyContent = IOUtils.toByteArray(request.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read body of request", e);
        }
        
        this.bodyContentType = request.getContentType();
    }
    
    @Override
    public final Method getMethod() {
        return method;
    }
    
    @Override
    public final String getPath() {
        return path;
    }
    
    @Override
    public final Map<String, Collection<String>> getParams() {
        return Collections.unmodifiableMap(params.asMap());
    }
    
    @Override
    public final Map<String, Object> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
    
    @Override
    public final byte[] getBodyContent() {
        return bodyContent;
    }
    
    @Override
    public final String getBodyContentType() {
        return bodyContentType;
    }
    
    /**
     * toString.
     * 
     * @return a String representation of the request
     */
    @Override
    public String toString() {
        
        String paramsJoined = Joiner.on(",").withKeyValueSeparator("=").useForNull("<null>").join(getParams());
        String headersJoined = Joiner.on(",").withKeyValueSeparator(": ").useForNull("<null>").join(getHeaders());
        
        return "HttpRealRequest: "
                + method + " " + path + "; "
                + "PARAMS: [" + paramsJoined + "]; "
                + "HEADERS: [" + headersJoined + "]; "
                + "CONTENT TYPE " + bodyContentType + "; "
                + "BODY " + new String(bodyContent) + ";";
    }
}
