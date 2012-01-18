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
package com.github.restdriver.serverdriver;

import static org.hamcrest.Matchers.*;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.matchers.HasJsonPath;
import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;
import com.github.restdriver.serverdriver.matchers.HasHeader;
import com.github.restdriver.serverdriver.matchers.HasHeaderWithValue;
import com.github.restdriver.serverdriver.matchers.HasResponseBody;
import com.github.restdriver.serverdriver.matchers.HasStatusCode;
import com.github.restdriver.serverdriver.matchers.Rfc1123DateMatcher;

/**
 * Class to help easy & fluent use of our matchers.
 */
public final class Matchers {
    
    private Matchers() {
    }
    
    /**
     * Creates a new instance of HasStatusCode.
     * 
     * @param statusCode The status code to match
     * @return The new matcher
     */
    public static TypeSafeMatcher<Response> hasStatusCode(int statusCode) {
        return new HasStatusCode(is(statusCode));
    }
    
    /**
     * Creates a new instance of HasStatusCode.
     * 
     * @param statusCodeMatcher The matcher against which the status code will be evaluated
     * @return The new matcher
     */
    public static TypeSafeMatcher<Response> hasStatusCode(Matcher<Integer> statusCodeMatcher) {
        return new HasStatusCode(statusCodeMatcher);
    }
    
    /**
     * Creates a new instance of HasResponseBody.
     * 
     * @param bodyMatcher The matcher against which the response body will be evaluated
     * @return The new matcher
     */
    public static TypeSafeMatcher<Response> hasResponseBody(Matcher<String> bodyMatcher) {
        return new HasResponseBody(bodyMatcher);
    }
    
    /**
     * Creates a new instance of HasHeader.
     * 
     * @param header The name of the header to check for the presence of - or name and value separated by ":"
     * @return The new matcher
     */
    public static TypeSafeMatcher<Response> hasHeader(String header) {
        
        if (header.indexOf(":") != -1) {
            return hasHeader(new Header(header));
        }
        
        return new HasHeader(header);
    }
    
    /**
     * Creates a new instance of HasHeader.
     * 
     * @param header The header to check for the presence of
     * @return The new matcher
     */
    public static TypeSafeMatcher<Response> hasHeader(Header header) {
        return hasHeader(header.getName(), header.getValue());
    }
    
    /**
     * Synonym for {@link HasHeaderWithValue}, using exact match for the value.
     * 
     * @param name The name of the header to check for the presence of
     * @param value The the header value to check
     * @return The new matcher
     */
    public static TypeSafeMatcher<Response> hasHeader(String name, String value) {
        return new HasHeaderWithValue(name, equalTo(value));
    }
    
    /**
     * Synonym for {@link HasHeaderWithValue}, using matcher for the value.
     * 
     * @param name The name of the header to check for the presence and value of
     * @param valueMatcher The matcher against which the header value will be evaluated
     * @return The new matcher
     */
    public static TypeSafeMatcher<Response> hasHeader(String name, Matcher<String> valueMatcher) {
        return new HasHeaderWithValue(name, valueMatcher);
    }
    
    /**
     * Creates a new instance of HasHeaderWithValue.
     * 
     * @param name The name of the header to check for the presence and value of
     * @param valueMatcher The matcher against which the header value will be evaluated
     * @return The new matcher
     */
    public static TypeSafeMatcher<Response> hasHeaderWithValue(String name, Matcher<String> valueMatcher) {
        return new HasHeaderWithValue(name, valueMatcher);
    }
    
    /**
     * Checks whether a header's value is a valid date according to RFC1123. All headers containing dates (Date, Expires, etc) should
     * be valid.
     * 
     * @return The new matcher.
     */
    public static TypeSafeMatcher<Header> isValidDateHeader() {
        return new Rfc1123DateMatcher();
    }
    
    /**
     * Checks whether a header's value is a valid date according to RFC1123. All headers containing dates (Date, Expires, etc) should
     * be valid.
     * 
     * @return The new matcher.
     */
    public static TypeSafeMatcher<Header> isRfc1123Compliant() {
        return new Rfc1123DateMatcher();
    }
    
    /**
     * Checks whether the given JSON object matches the JSONpath.
     * 
     * No matcher is used on the matched value. It is based only on the existence of something at the given JSONpath.
     * 
     * @param jsonPath The JSONpath to match.
     * @param <T> The type of the matcher.
     * @return The new matcher.
     */
    public static <T> TypeSafeMatcher<JsonNode> hasJsonPath(String jsonPath) {
        return new HasJsonPath<T>(jsonPath);
    }
    
    /**
     * Checks whether the given JSON object matches the JSONpath. NB when asserting on numeric values you will *have* to use Longs and
     * Doubles, or face the wrath of the ClassCastException!
     * 
     * @param jsonPath The JSONpath to match.
     * @param matcher The matcher to apply to the result of the JSONpath.
     * @param <T> The type of the matcher.
     * @return The new matcher.
     */
    public static <T> TypeSafeMatcher<JsonNode> hasJsonPath(String jsonPath, Matcher<T> matcher) {
        return new HasJsonPath<T>(jsonPath, matcher);
    }
}
