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
package com.github.restdriver.serverdriver.matchers;


import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.exception.RuntimeDateFormatException;
import java.time.ZonedDateTime;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import java.time.format.DateTimeParseException;

/**
 * Matcher to check that headers contain dates which are spec-valid. All dates in HTTP headers (Date-header, caching, etc) should
 * pass this matcher.
 */
public final class Rfc1123DateMatcher extends TypeSafeMatcher<Header> {
    
    /**
     * Parse a string as if it is an RFC1123-compliant date.
     * 
     * @param rawString The original String.
     * @return The DateTime object set to UTC.
     */
    public ZonedDateTime getDateTime(String rawString) {
        try {
            return ZonedDateTime.parse(rawString, RFC_1123_DATE_TIME);
        } catch (DateTimeParseException pe) {
            throw new RuntimeDateFormatException(pe);
        }
    }
    
    @Override
    protected boolean matchesSafely(Header dateHeader) {
        
        try {
            getDateTime(dateHeader.getValue());
            return true;
            
        } catch (RuntimeDateFormatException pe) {
            return false;
            
        }
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText("Rfc1123-compliant date in header, like 'Mon, 09 May 2011 18:49:18 GMT'");
    }
    
}
