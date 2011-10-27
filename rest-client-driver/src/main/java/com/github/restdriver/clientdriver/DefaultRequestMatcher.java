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
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.github.restdriver.clientdriver.exception.ClientDriverInternalException;

/**
 * Implementation of {@link RequestMatcher}. This implementation expects exact match in terms of the HTTP method, the
 * path &amp; query string, and any body of the request.
 */
public final class DefaultRequestMatcher implements RequestMatcher {
    
    /**
     * Checks for a match between an actual {@link ClientDriverRequest} and an expected {@link ClientDriverRequest}. This
     * implementation is as strict as it can be with exact matching for Strings, but can also use regular expressions in
     * the form of Patterns.
     * 
     * @param actualRequest
     *            The actual request {@link ClientDriverRequest}
     * @param expectedRequest
     *            The expected {@link ClientDriverRequest}
     * @return True if there is a match, falsetto otherwise.
     */
    @SuppressWarnings("unchecked")
    public boolean isMatch(ClientDriverRequest actualClientDriverRequest, ClientDriverRequest expectedRequest) {
        
        // TODO: Better diagnostics from this method. See https://github.com/rest-driver/rest-driver/issues/7
        
        // same method?
        if (actualClientDriverRequest.getMethod() != expectedRequest.getMethod()) {
            return false;
        }
        // same base path?
        // The actual request will always be a string as it is a 'real';
        if (!isStringOrPatternMatch((String) actualClientDriverRequest.getPath(), expectedRequest.getPath())) {
            return false;
        }
        
        Map<String, Collection<Object>> actualParams = actualClientDriverRequest.getParams();
        Map<String, Collection<Object>> expectedParams = expectedRequest.getParams();
        
        // same number of query-string parameters?
        if (actualParams.size() != expectedParams.size()) {
            return false;
        }
        
        for (String expectedKey : expectedParams.keySet()) {
            
            Collection<Object> actualParamValues = actualParams.get(expectedKey);
            
            if (actualParamValues == null || actualParamValues.size() == 0) {
                return false;
            }
            
            Collection<Object> expectedParamValues = expectedParams.get(expectedKey);
            
            if (expectedParamValues.size() != actualParamValues.size()) {
                return false;
            }
            
            for (Object actualParamValue : actualParamValues) {
                
                if (actualParamValue instanceof String || actualParamValue == null) {
                    
                    final String strActualValue = (String) actualParamValue;
                    
                    boolean matched = false;
                    
                    for (Object expectedParamValue : expectedParamValues) {
                        
                        if (isStringOrPatternMatch(strActualValue, expectedParamValue)) {
                            matched = true;
                        }
                    }
                    
                    if (!matched) {
                        return false;
                    }
                } else {
                    throw new ClientDriverInternalException("Expected all params on incomming request to be strings", null);
                }
            }
            
        }
        
        // same keys/values in headers map?
        Map<String, Object> expectedHeaders = expectedRequest.getHeaders();
        Map<String, Object> actualHeaders = actualClientDriverRequest.getHeaders();
        
        for (String expectedHeaderName : expectedHeaders.keySet()) {
            
            Object expectedHeaderValue = expectedHeaders.get(expectedHeaderName);
            
            boolean matched = false;
            
            for (Entry<String, Object> actualHeader : actualHeaders.entrySet()) {
                Object value = actualHeader.getValue();
                if (value instanceof Enumeration) {
                    Enumeration<String> valueEnumeration = (Enumeration<String>) value;
                    while (valueEnumeration.hasMoreElements()) {
                        String currentValue = valueEnumeration.nextElement();
                        if (isStringOrPatternMatch(currentValue, expectedHeaderValue)) {
                            matched = true;
                            break;
                        }
                    }
                    
                } else {
                    
                    if (isStringOrPatternMatch((String) value, expectedHeaderValue)) {
                        matched = true;
                        break;
                    }
                }
            }
            
            if (!matched) {
                return false;
            }
            
        }
        
        // same request body?
        if (expectedRequest.getBodyContent() != null) {
            
            // this is needed because clients have a habit of putting
            // "text/html; charset=UTF-8" when you only ask for "text/html".
            String actualContentType = (String) actualClientDriverRequest.getBodyContentType();
            if (actualContentType.contains(";")) {
                actualContentType = actualContentType.substring(0, actualContentType.indexOf(';'));
            }
            
            // same type?
            if (!isStringOrPatternMatch(actualContentType, expectedRequest.getBodyContentType())) {
                return false;
            }
            
            // same content?
            if (!isStringOrPatternMatch((String) actualClientDriverRequest.getBodyContent(), expectedRequest.getBodyContent())) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isStringOrPatternMatch(String actual, Object expected) {
        if (actual == null) {
            actual = "";
        }
        
        if (expected instanceof String) {
            
            return actual.equals(expected);
            
        } else if (expected instanceof Pattern) {
            
            Pattern pattern = (Pattern) expected;
            return pattern.matcher(actual).matches();
            
        } else {
            throw new ClientDriverInternalException("DefaultRequestMatcher asked to match " + expected.getClass()
                    + ", but only knows String and Pattern.", null);
        }
    }
    
}
