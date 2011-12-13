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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.restdriver.clientdriver.exception.ClientDriverInternalException;

/**
 * Implementation of {@link RequestMatcher}. This implementation expects exact match in terms of the HTTP method, the
 * path &amp; query string, and any body of the request.
 */
public final class DefaultRequestMatcher implements RequestMatcher {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestMatcher.class);
    
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
    public boolean isMatch(ClientDriverRequest actualRequest, ClientDriverRequest expectedRequest) {
        
        // TODO: Better diagnostics from this method. See https://github.com/rest-driver/rest-driver/issues/7
        
        boolean sameMethod = isSameMethod(actualRequest, expectedRequest);
        
        if (!sameMethod) {
            return false;
        }
        
        boolean sameBasePath = isSameBasePath(actualRequest, expectedRequest);
        
        if (!sameBasePath) {
            return false;
        }
        
        boolean sameQueryString = hasSameQueryString(actualRequest, expectedRequest);
        
        if (!sameQueryString) {
            return false;
        }
        
        boolean sameHeaders = hasSameHeaders(actualRequest, expectedRequest);
        
        if (!sameHeaders) {
            return false;
        }
        
        boolean sameBody = hasSameBody(actualRequest, expectedRequest);
        
        if (!sameBody) {
            return false;
        }
        
        return true;
    }
    
    private boolean isSameMethod(ClientDriverRequest actualRequest, ClientDriverRequest expectedRequest) {
        
        if (actualRequest.getMethod() != expectedRequest.getMethod()) {
            LOGGER.info("REJECTED on method: expected " + expectedRequest.getMethod() + " != " + actualRequest.getMethod());
            return false;
        }
        
        return true;
    }
    
    private boolean isSameBasePath(ClientDriverRequest actualRequest, ClientDriverRequest expectedRequest) {
        
        // The actual request will always be a string as it is a 'real';
        if (!isStringOrPatternMatch((String) actualRequest.getPath(), expectedRequest.getPath())) {
            LOGGER.info("REJECTED on path: expected " + expectedRequest.getPath() + " != " + actualRequest.getPath());
            return false;
        }
        
        return true;
    }
    
    private boolean hasSameQueryString(ClientDriverRequest actualRequest, ClientDriverRequest expectedRequest) {
        
        Map<String, Collection<Object>> actualParams = actualRequest.getParams();
        Map<String, Collection<Object>> expectedParams = expectedRequest.getParams();
        
        if (actualParams.size() != expectedParams.size()) {
            LOGGER.info("REJECTED on number of params: expected " + expectedParams.size() + " != " + actualParams.size());
            return false;
        }
        
        for (String expectedKey : expectedParams.keySet()) {
            
            Collection<Object> actualParamValues = actualParams.get(expectedKey);
            
            if (actualParamValues == null || actualParamValues.size() == 0) {
                LOGGER.info("REJECTED on missing param key: expected " + expectedKey + "=" + expectedParams.get(expectedKey));
                return false;
            }
            
            Collection<Object> expectedParamValues = expectedParams.get(expectedKey);
            
            if (expectedParamValues.size() != actualParamValues.size()) {
                LOGGER.info("REJECTED on number of values for param '" + expectedKey + "': expected " + expectedParamValues.size() + " != " + actualParamValues.size());
                return false;
            }
            
            boolean sameParamValues = areTheSame(expectedKey, actualParamValues, expectedParamValues);
            
            if (!sameParamValues) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean areTheSame(String expectedKey, Collection<Object> actualParamValues, Collection<Object> expectedParamValues) {
        
        for (Object expectedParamValue : expectedParamValues) {
            
            boolean matched = false;
            
            for (Object actualParamValue : actualParamValues) {
                if (actualParamValue instanceof String || actualParamValue == null) {
                    
                    String actualValue = (String) actualParamValue;
                    if (isStringOrPatternMatch(actualValue, expectedParamValue)) {
                        matched = true;
                    }
                } else {
                    throw new ClientDriverInternalException("Expected all params on incoming request to be strings", null);
                }
            }
            
            if (!matched) {
                LOGGER.info("REJECTED on unmatched params key: expected " + expectedKey + "=" + expectedParamValue);
                return false;
            }
        }
        
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean hasSameHeaders(ClientDriverRequest actualRequest, ClientDriverRequest expectedRequest) {
        
        Map<String, Object> expectedHeaders = expectedRequest.getHeaders();
        Map<String, Object> actualHeaders = actualRequest.getHeaders();
        
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
                if (expectedHeaderValue instanceof String) {
                    LOGGER.info("REJECTED on missing header: expected " + expectedHeaderName + "=" + (String) expectedHeaderValue);
                } else {
                    LOGGER.info("REJECTED on missing header: expected " + expectedHeaderName + "=" + (Pattern) expectedHeaderValue);
                }
                return false;
            }
            
        }
        
        return true;
    }
    
    private boolean hasSameBody(ClientDriverRequest actualRequest, ClientDriverRequest expectedRequest) {
        
        if (expectedRequest.getBodyContent() != null) {
            
            // this is needed because clients have a habit of putting
            // "text/html; charset=UTF-8" when you only ask for "text/html".
            String actualContentType = (String) actualRequest.getBodyContentType();
            if (actualContentType.contains(";")) {
                actualContentType = actualContentType.substring(0, actualContentType.indexOf(';'));
            }
            
            if (!isStringOrPatternMatch(actualContentType, expectedRequest.getBodyContentType())) {
                if (expectedRequest.getBodyContentType() instanceof String) {
                    LOGGER.info("REJECTED on content type: expected " + (String) expectedRequest.getBodyContentType() + ", actual " + (String) actualContentType);
                } else {
                    LOGGER.info("REJECTED on content type: expected " + ((Pattern) expectedRequest.getBodyContentType()).pattern() + ", actual " + (String) actualContentType);
                }
                return false;
            }
            
            
            if (!isStringOrPatternMatch((String) actualRequest.getBodyContent(), expectedRequest.getBodyContent())) {
                if (expectedRequest.getBodyContent() instanceof String) {
                    LOGGER.info("REJECTED on content: expected " + (String) expectedRequest.getBodyContent() + ", actual " + (String) actualRequest.getBodyContent());
                } else {
                    LOGGER.info("REJECTED on content: expected " + ((Pattern) expectedRequest.getBodyContent()).pattern() + ", actual " + (String) actualRequest.getBodyContent());
                }
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
