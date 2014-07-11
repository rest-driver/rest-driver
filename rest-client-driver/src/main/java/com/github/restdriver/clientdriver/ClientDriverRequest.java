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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import com.github.restdriver.clientdriver.capture.BodyCapture;
import com.github.restdriver.matchers.MatchesRegex;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Class for encapsulating an HTTP request.
 */
public final class ClientDriverRequest {
    
    private static final String CONTENT_TYPE = "Content-Type";
    
    /**
     * HTTP method enum for specifying which method you expect to be called with.
     */
    public enum Method {
        GET, POST, PUT, DELETE, OPTIONS, HEAD, TRACE
    }
    
    private final Matcher<? extends String> path;
    private final Multimap<String, Matcher<? extends String>> params;
    private final Map<String, Matcher<? extends String>> headers;
    private final Set<String> excludedHeaders;
    
    private Method method;
    private Matcher<? extends String> bodyContentMatcher;
    private Matcher<? extends String> bodyContentType;
    private boolean anyParams;
    private BodyCapture<?> bodyCapture;
    
    /**
     * Constructor taking String matcher.
     * 
     * @param path The mandatory argument is the path which will be listened on
     */
    public ClientDriverRequest(Matcher<? extends String> path) {
        this.path = path;
        method = Method.GET;
        params = HashMultimap.create();
        headers = new HashMap<String, Matcher<? extends String>>();
        excludedHeaders = new HashSet<String>();
        anyParams = false;
    }
    
    /**
     * Constructor taking String.
     * 
     * @param path The mandatory argument is the path which will be listened on
     */
    public ClientDriverRequest(String path) {
        this(new IsEqual<String>(path));
    }
    
    /**
     * Constructor taking Pattern.
     * 
     * @param path The mandatory argument is the path which will be listened on
     */
    public ClientDriverRequest(Pattern path) {
        this(new MatchesRegex(path));
    }
    
    /**
     * Get the path.
     * 
     * @return the path which requests are expected on.
     */
    public Matcher<? extends String> getPath() {
        return path;
    }
    
    /**
     * @param withMethod the method to set
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withMethod(Method withMethod) {
        this.method = withMethod;
        return this;
    }
    
    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }
    
    /**
     * Setter for expecting any number of querystring parameters with any values.
     * With this set, any expected parameters are ignored.
     * 
     * @return the request
     */
    public ClientDriverRequest withAnyParams() {
        anyParams = true;
        return this;
    }
    
    /**
     * Setter for expecting query-string parameters on the end of the url.
     * 
     * @param key The key from ?key=value
     * @param value The value from ?key=value in the form of a String
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withParam(String key, String value) {
        params.put(key, new IsEqual<String>(value));
        return this;
    }
    
    /**
     * Setter for expecting query-string parameters on the end of the url.
     * 
     * @param key The key from ?key=value
     * @param value The value from ?key=value in the form of a String
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withParam(String key, int value) {
        return withParam(key, String.valueOf(value));
    }
    
    /**
     * Setter for expecting query-string parameters on the end of the url.
     * 
     * @param key The key from ?key=value
     * @param value The value from ?key=value in the form of a String
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withParam(String key, long value) {
        return withParam(key, String.valueOf(value));
    }
    
    /**
     * Setter for expecting query-string parameters on the end of the url.
     * 
     * @param key The key from ?key=value
     * @param value The value from ?key=value in the form of a String
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withParam(String key, boolean value) {
        return withParam(key, String.valueOf(value));
    }
    
    /**
     * Setter for expecting query-string parameters on the end of the url.
     * 
     * @param key The key from ?key=value
     * @param value The value from ?key=value in the form of a String
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withParam(String key, Object value) {
        return withParam(key, value.toString());
    }
    
    /**
     * Setter for expecting query-string parameters on the end of the url.
     * 
     * @param key The key from ?key=value
     * @param value The value from ?key=value in the form of a Pattern
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withParam(String key, Pattern value) {
        params.put(key, new MatchesRegex(value));
        return this;
    }
    
    /**
     * Setter for expecting query-string parameters on the end of the url.
     * 
     * @param key The key from ?key=value
     * @param value The value from ?key=value in the form of a Matcher
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withParam(String key, Matcher<? extends String> value) {
        params.put(key, value);
        return this;
    }
    
    /**
     * Setter for expecting multiple query-string parameters on the end of the url.
     * 
     * @param newParams The map of key value pairs from ?key=value
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withParams(Map<String, Object> newParams) {
        for (Entry<String, Object> entry : newParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Pattern) {
                this.params.put(key, new MatchesRegex((Pattern) value));
            } else {
                this.params.put(key, new IsEqual<String>(value.toString()));
            }
        }
        return this;
    }
    
    /**
     * @return the params
     */
    Map<String, Collection<Matcher<? extends String>>> getParams() {
        return params.asMap();
    }
    
    /**
     * @return the anyParams
     */
    boolean getAnyParams() {
        return anyParams;
    }
    
    /**
     * toString.
     * 
     * @return a String representation of the request
     */
    @Override
    public String toString() {
        
        String paramsJoined = Joiner.on(",").withKeyValueSeparator("=").join(params.asMap());
        String headersJoined = Joiner.on(",").withKeyValueSeparator(": ").join(headers);
        String excludedHeadersJoined = Joiner.on(",").join(excludedHeaders);
        
        return "ClientDriverRequest: "
                + method + " " + path.toString() + "; "
                + "ANY PARAMS: " + anyParams + "; "
                + "PARAMS: [" + paramsJoined + "]; "
                + "HEADERS: [" + headersJoined + "]; "
                + "NOT HEADERS: [" + excludedHeadersJoined + "]; "
                + "CONTENT TYPE " + bodyContentType + "; "
                + "BODY " + bodyContentMatcher + ";";
    }
    
    /**
     * @return The body content matcher
     */
    public Matcher<? extends String> getBodyContentMatcher() {
        return bodyContentMatcher;
    }
    
    /**
     * @return the bodyContentType
     */
    public Matcher<? extends String> getBodyContentType() {
        return bodyContentType;
    }
    
    /**
     * Setter for expecting body content and type, where content is in the form of a String and type is in the form of a
     * String.
     * 
     * @param withBodyContent the bodyContent to set
     * @param withContentType eg "text/plain"
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withBody(String withBodyContent, String withContentType) {
        bodyContentMatcher = new IsEqual<String>(withBodyContent);
        bodyContentType = new IsEqual<String>(withContentType);
        return this;
    }
    
    /**
     * Setter for expecting body content and type, where content is in the form of a String and type is in the form of a
     * Pattern.
     * 
     * @param withBodyContent the bodyContent to set
     * @param contentType eg "text/plain"
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withBody(String withBodyContent, Pattern contentType) {
        bodyContentMatcher = new IsEqual<String>(withBodyContent);
        bodyContentType = new MatchesRegex(contentType);
        return this;
    }
    
    /**
     * Setter for expecting body content and type, where content is in the form of a Pattern and type is in the form of
     * a String.
     * 
     * @param withBodyContent the bodyContent to set
     * @param contentType eg "text/plain"
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withBody(Pattern withBodyContent, String contentType) {
        bodyContentMatcher = new MatchesRegex(withBodyContent);
        bodyContentType = new IsEqual<String>(contentType);
        return this;
    }
    
    /**
     * Setter for expecting body content and type, where content is in the form of a Pattern and type is in the form of
     * a Pattern.
     * 
     * @param withBodyContent the bodyContent to set
     * @param contentType eg "text/plain"
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withBody(Pattern withBodyContent, Pattern contentType) {
        bodyContentMatcher = new MatchesRegex(withBodyContent);
        bodyContentType = new MatchesRegex(contentType);
        return this;
    }
    
    /**
     * Setter for expecting body content and type, where content is in the form of a Matcher and type is in the form of
     * a Pattern.
     * 
     * @param bodyContentMatcher the Matcher&lt;String&gt; to use to set
     * @param contentType eg "text/plain"
     * @return the object you called the method on, so you can chain these calls.
     */
    public ClientDriverRequest withBody(Matcher<? extends String> bodyContentMatcher, String contentType) {
        this.bodyContentMatcher = bodyContentMatcher;
        this.bodyContentType = new IsEqual<String>(contentType);
        return this;
    }
    
    /**
     * Setter for adding a {@link BodyCapture} to the expectation for later assertions/debugging.
     * 
     * @param bodyCapture the capturing object.
     * @return this, for chaining.
     */
    public ClientDriverRequest capturingBodyIn(BodyCapture<?> bodyCapture) {
        this.bodyCapture = bodyCapture;
        return this;
    }
    
    public BodyCapture<?> getBodyCapture() {
        return bodyCapture;
    }
    
    /**
     * Setter for expecting a specific header name and value matcher.
     * 
     * @param withHeaderName the headerName to match on
     * @param headerValueMatcher the matcher to use for the header value
     * @return the object you called the method on, so you can chain these calls
     */
    public ClientDriverRequest withHeader(String withHeaderName, Matcher<? extends String> headerValueMatcher) {
        if (CONTENT_TYPE.equalsIgnoreCase(withHeaderName)) {
            bodyContentType = headerValueMatcher;
        } else {
            headers.put(withHeaderName.toLowerCase(), headerValueMatcher);
        }
        return this;
    }
    
    /**
     * Setter for expecting a specific header name and value pair.
     * 
     * @param withHeaderName the headerName to match on
     * @param withHeaderValue the headerValue to match on
     * @return the object you called the method on, so you can chain these calls
     */
    public ClientDriverRequest withHeader(String withHeaderName, String withHeaderValue) {
        return withHeader(withHeaderName, new IsEqual<String>(withHeaderValue));
    }
    
    /**
     * Setter for expecting a specific header name not to be present on the request.
     * 
     * @param withoutHeaderName the headerName to match on
     * @return the object you called the method on, so you can chain these calls
     */
    public ClientDriverRequest withoutHeader(String withoutHeaderName) {
        excludedHeaders.add(withoutHeaderName);
        return this;
    }
    
    /**
     * Setter for expecting a specific header name and value pair, where value is in the form of a Pattern.
     * 
     * @param withHeaderName the headerName to match on
     * @param withHeaderValue the headerValue to match on
     * @return the object you called the method on, so you can chain these calls
     */
    public ClientDriverRequest withHeader(String withHeaderName, Pattern withHeaderValue) {
        return withHeader(withHeaderName, new MatchesRegex(withHeaderValue));
    }
    
    /**
     * Setter for expecting a map of header name and value pairs.
     * 
     * @param headers a map of header names to header values to match on
     * @return the object you called the method on, so you can chain these calls
     */
    public ClientDriverRequest withHeaders(Map<String, Object> headers) {
        for (Entry<String, Object> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            Object headerValue = entry.getValue();
            if (headerValue instanceof Pattern) {
                withHeader(headerName, new MatchesRegex((Pattern) headerValue));
            } else {
                withHeader(headerName, new IsEqual<String>(headerValue.toString()));
            }
        }
        return this;
    }
    
    public ClientDriverRequest withBasicAuth(String username, String password) {
        headers.put("Authorization", new IsEqual<String>("Basic " + base64(username + ":" + password)));
        return this;
    }
    
    /**
     * @return the headers
     */
    public Map<String, Matcher<? extends String>> getHeaders() {
        return headers;
    }
    
    /**
     * @return the excluded headers
     */
    public Set<String> getExcludedHeaders() {
        return excludedHeaders;
    }
    
    private static String base64(String content) {
        return new String(Base64.encodeBase64(content.getBytes()));
    }
    
}
