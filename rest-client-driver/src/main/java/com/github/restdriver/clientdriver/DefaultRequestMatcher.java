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
import java.util.Set;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link RequestMatcher}. This implementation expects exact match in terms of the HTTP method, the
 * path &amp; query string, and any body of the request.
 */
public final class DefaultRequestMatcher implements RequestMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestMatcher.class);

    @Override
    public boolean isMatch(RealRequest realRequest, ClientDriverRequest expectedRequest) {

        if (!isSameMethod(realRequest, expectedRequest)) {
            return false;
        }

        if (!isSameBasePath(realRequest, expectedRequest)) {
            return false;
        }

        if (!hasSameQueryString(realRequest, expectedRequest)) {
            return false;
        }
        
        if (hasExcludedHeaders(realRequest, expectedRequest)) {
            return false;
        }

        if (!hasSameHeaders(realRequest, expectedRequest)) {
            return false;
        }

        if (!hasSameBody(realRequest, expectedRequest)) {
            return false;
        }

        captureBodyIfRequired(realRequest, expectedRequest);

        return true;
    }

    private void captureBodyIfRequired(RealRequest realRequest, ClientDriverRequest expectedRequest) {
        if (expectedRequest.getBodyCapture() != null) {
            expectedRequest.getBodyCapture().setBody(realRequest.getBodyContent());
        }
    }

    private boolean isSameMethod(RealRequest realRequest, ClientDriverRequest expectedRequest) {

        if (realRequest.getMethod() != expectedRequest.getMethod()) {
            LOGGER.info("({} {}) REJECTED on method: expected {} != {}", realRequest.getMethod(), realRequest.getPath(), expectedRequest.getMethod(), realRequest.getMethod());
            return false;
        }

        return true;
    }

    private boolean isSameBasePath(RealRequest realRequest, ClientDriverRequest expectedRequest) {

        if (!expectedRequest.getPath().matches(realRequest.getPath())) {
            LOGGER.info("({} {}) REJECTED on path: expected {} != {}", realRequest.getMethod(), realRequest.getPath(), expectedRequest.getPath(), realRequest.getPath());
            return false;
        }

        return true;
    }

    private boolean hasSameQueryString(RealRequest realRequest, ClientDriverRequest expectedRequest) {

        if (expectedRequest.getAnyParams()) {
            return true;
        }

        Map<String, Collection<String>> actualParams = realRequest.getParams();
        Map<String, Collection<Matcher<? extends String>>> expectedParams = expectedRequest.getParams();

        if (actualParams.size() != expectedParams.size()) {
            LOGGER.info("({} {}) REJECTED on number of params: expected {} != {}", realRequest.getMethod(), realRequest.getPath(), expectedParams.size(), actualParams.size());
            return false;
        }

        for (String expectedKey : expectedParams.keySet()) {

            Collection<String> actualParamValues = actualParams.get(expectedKey);

            if (actualParamValues == null || actualParamValues.size() == 0) {
                LOGGER.info("({} {}) REJECTED on missing param key: expected {} = {}", realRequest.getMethod(), realRequest.getPath(), expectedKey, expectedParams.get(expectedKey));
                return false;
            }

            Collection<Matcher<? extends String>> expectedParamValues = expectedParams.get(expectedKey);

            if (expectedParamValues.size() != actualParamValues.size()) {
                LOGGER.info("({} {}) REJECTED on number of values for param '{}': expected {} != {}", realRequest.getMethod(), realRequest.getPath(), expectedKey, expectedParamValues.size(), actualParamValues.size());
                return false;
            }

            boolean sameParamValues = containsMatch(realRequest, expectedKey, actualParamValues, expectedParamValues);

            if (!sameParamValues) {
                return false;
            }
        }

        return true;
    }

    private boolean containsMatch(RealRequest realRequest, String expectedKey, Collection<String> actualParamValues, Collection<Matcher<? extends String>> expectedParamValues) {

        for (Matcher<? extends String> expectedParamValue : expectedParamValues) {

            boolean matched = false;

            for (String actualParamValue : actualParamValues) {
                if (expectedParamValue.matches(actualParamValue)) {
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                LOGGER.info("({} {}) REJECTED on unmatched params key: expected {} = {}", realRequest.getMethod(), realRequest.getPath(), expectedKey, expectedParamValue);
                return false;
            }
        }

        return true;
    }
    
    private boolean hasExcludedHeaders(RealRequest realRequest, ClientDriverRequest expectedRequest) {
        Set<String> excludedHeaders = expectedRequest.getExcludedHeaders();
        Map<String, Object> actualHeaders = realRequest.getHeaders();
        
        for (String excludedHeader : excludedHeaders) {
            if (actualHeaders.containsKey(excludedHeader)) {
                return true;
            }
        }
        
        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean hasSameHeaders(RealRequest realRequest, ClientDriverRequest expectedRequest) {

        Map<String, Matcher<? extends String>> expectedHeaders = expectedRequest.getHeaders();
        Map<String, Object> actualHeaders = realRequest.getHeaders();

        for (String expectedHeaderName : expectedHeaders.keySet()) {

            Matcher<? extends String> expectedHeaderValue = expectedHeaders.get(expectedHeaderName);

            boolean matched = false;

            for (Entry<String, Object> actualHeader : actualHeaders.entrySet()) {
                Object value = actualHeader.getValue();
                if (value instanceof Enumeration) {
                    Enumeration<String> valueEnumeration = (Enumeration<String>) value;
                    while (valueEnumeration.hasMoreElements()) {
                        String currentValue = valueEnumeration.nextElement();
                        if (expectedHeaderValue.matches(currentValue)) {
                            matched = true;
                            break;
                        }
                    }

                } else {
                    if (expectedHeaderValue.matches((String) value)) {
                        matched = true;
                        break;
                    }
                }
            }

            if (!matched) {
                LOGGER.info("({} {}) REJECTED on missing header: expected {} = {}", realRequest.getMethod(), realRequest.getPath(), expectedHeaderName, expectedHeaderValue);
                return false;
            }

        }

        return true;
    }

    private boolean hasSameBody(RealRequest realRequest, ClientDriverRequest expectedRequest) {

        if (expectedRequest.getBodyContentType() != null) {
            String actualContentType = realRequest.getBodyContentType();
            if (actualContentType == null) {
                return false;
            }

            // this is needed because clients have a habit of putting
            // "text/html; charset=UTF-8" when you only ask for "text/html".
            if (actualContentType.contains(";")) {
                actualContentType = actualContentType.substring(0, actualContentType.indexOf(';'));
            }

            if (!expectedRequest.getBodyContentType().matches(actualContentType)) {
                LOGGER.info("({} {}) REJECTED on content type: expected {}, actual {}", realRequest.getMethod(), realRequest.getPath(), expectedRequest.getBodyContentType(), (String) actualContentType);
                return false;
            }
        }

        if (expectedRequest.getBodyContentMatcher() != null) {
            String actualContent = new String(realRequest.getBodyContent());

            boolean hasMatchingBodyContent = expectedRequest.getBodyContentMatcher().matches(actualContent);

            if (!hasMatchingBodyContent) {
                StringDescription description = new StringDescription();
                expectedRequest.getBodyContentMatcher().describeTo(description);
                description.appendText(" ");
                expectedRequest.getBodyContentMatcher().describeMismatch(actualContent, description);
                LOGGER.info("({} {}) REJECTED on content: Expected {}", realRequest.getMethod(), realRequest.getPath(), description.toString());
                return false;
            }

        }

        return true;
    }

}
