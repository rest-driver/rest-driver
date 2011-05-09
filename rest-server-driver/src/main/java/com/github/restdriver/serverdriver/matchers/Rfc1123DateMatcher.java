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
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * User: mjg
 * Date: 09/05/11
 * Time: 20:16
 */
public class Rfc1123DateMatcher extends TypeSafeMatcher<Header> {

    @Override
    protected boolean matchesSafely(Header dateHeader) {

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

        try {
            formatter.parse(dateHeader.getValue());
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
//            description.appendText("Response with header named '" + name + "'");
    }
    
}
