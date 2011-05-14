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

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.exception.RuntimeDateFormatException;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Matcher to check that headers contain dates which are spec-valid.  All dates in HTTP headers (Date-header, caching, etc) should
 * pass this matcher.
 */
public final class Rfc1123DateMatcher extends TypeSafeMatcher<Header> {

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     * Parse a string as if it is an RFC1123-compliant date.
     *
     * @param rawString The original String.
     * @return The DateTime object set to UTC.
     */
    public DateTime getDateTime(String rawString) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setLenient(false); // This stops well-formatted but invalid dates like Feb 31

        try {
            return new DateTime(formatter.parse(rawString)).toDateTime(DateTimeZone.UTC);

        } catch (ParseException pe) {
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
