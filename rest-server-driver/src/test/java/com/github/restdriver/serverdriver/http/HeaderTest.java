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
package com.github.restdriver.serverdriver.http;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

import com.github.restdriver.serverdriver.http.exception.RuntimeDateFormatException;

public class HeaderTest {
    
    @Test
    public void newlyCreatedHeaderHasCorrectName() {
        Header header = new Header("name", "value");
        assertThat(header.getName(), is("name"));
    }
    
    @Test
    public void newlyCreatedHeaderHasCorrectValue() {
        Header header = new Header("name", "value");
        assertThat(header.getValue(), is("value"));
    }
    
    @Test
    public void headerHasSensibleToString() {
        Header header = new Header("name", "value");
        assertThat(header.toString(), is("name: value"));
    }
    
    @Test
    public void headerHashCodeIsTheSameForEqualHeaders() {
        Header header1 = new Header("name", "value");
        Header header2 = new Header("name", "value");
        assertThat(header1.hashCode(), equalTo(header2.hashCode()));
    }
    
    @Test
    public void headerHashCodeIsDifferentForDifferentName() {
        Header header1 = new Header("name", "value");
        Header header2 = new Header("different", "value");
        assertThat(header1.hashCode(), not(equalTo(header2.hashCode())));
    }
    
    @Test
    public void headerHashCodeIsDifferentForDifferentValue() {
        Header header1 = new Header("name", "value");
        Header header2 = new Header("name", "different");
        assertThat(header1.hashCode(), not(equalTo(header2.hashCode())));
    }
    
    @Test
    public void headerIsEqualToItself() {
        Header header = new Header("name", "value");
        assertThat(header, equalTo(header));
    }
    
    @Test
    public void headerIsNotEqualToObjectOfAnotherType() {
        Header header = new Header("name", "value");
        assertThat(header.equals(""), is(false));
    }
    
    @Test
    public void headersAreEqualWithSameNameAndValue() {
        Header header1 = new Header("name", "value");
        Header header2 = new Header("name", "value");
        assertThat(header1, equalTo(header2));
    }
    
    @Test
    public void headersAreNotEqualWithDifferentName() {
        Header header1 = new Header("name", "value");
        Header header2 = new Header("different", "value");
        assertThat(header1, not(equalTo(header2)));
    }
    
    @Test
    public void headersAreNotEqualWithDifferentValue() {
        Header header1 = new Header("name", "value");
        Header header2 = new Header("name", "different");
        assertThat(header1, not(equalTo(header2)));
    }
    
    @Test
    public void headerWithNullNameAndValueHasHashCode() {
        Header header = new Header(null, null);
        assertThat(header.hashCode() > 0, is(true));
    }
    
    @Test
    public void headerWithSingleStringIsParsedCorrectly() {
        Header header = new Header("X-foo: blah");
        assertThat(header.getName(), is("X-foo"));
        assertThat(header.getValue(), is("blah"));
    }
    
    @Test
    public void headerWithSingleStringAndCrazyWhitespaceIsParsedCorrectly() {
        // NB this is not valid according to HTTP spec, but we allow it anyway.
        Header header = new Header("  X-foo  :   blah  ");
        assertThat(header.getName(), is("X-foo"));
        assertThat(header.getValue(), is("blah"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void headerWithSingleStringAndNoColonIsIllegal() {
        new Header("  X-foo ");
    }
    
    @Test
    public void headerWithSingleStringAndTwoColonsIsLegal() {
        Header header = new Header("  X-foo : yes : perhaps");
        
        assertThat(header.getName(), is("X-foo"));
        assertThat(header.getValue(), is("yes : perhaps"));
    }
    
    public void headerAppliesItselfToRequest() {
        HttpUriRequest request = new HttpGet();
        Header header = new Header("name", "value");
        header.applyTo(new ServerDriverHttpUriRequest(request));
        assertThat(request.getFirstHeader("name").getValue(), is("value"));
    }
    
    @Test
    public void headerNameIsCaseInsensitiveButValueIsnt() {
        Header upper = new Header("HELLO: there");
        Header lower = new Header("hello: there");
        Header lowerUpper = new Header("hello: THERE");
        
        assertThat(upper, equalTo(lower));
        assertThat(lower, not(equalTo(lowerUpper)));
    }
    
    @Test
    public void hashCodeTreatsNameAsCaseInsensitive() {
        Header upper = new Header("HELLO: there");
        Header lower = new Header("hello: there");
        
        assertThat(upper.hashCode(), equalTo(lower.hashCode()));
    }
    
    @Test
    public void asDateTimeReturnsCorrectDate() {
        Header dateHeader = new Header("HELLO: Mon, 09 May 2011 18:49:18 GMT");
        
        DateTime headerDate = dateHeader.asDateTime();
        
        assertThat(headerDate.getDayOfWeek(), is(DateTimeConstants.MONDAY));
        
        assertThat(headerDate.getDayOfMonth(), is(9));
        assertThat(headerDate.getMonthOfYear(), is(DateTimeConstants.MAY));
        assertThat(headerDate.getYear(), is(2011));
        
        assertThat(headerDate.getHourOfDay(), is(18));
        assertThat(headerDate.getMinuteOfHour(), is(49));
        assertThat(headerDate.getSecondOfMinute(), is(18));
        
    }
    
    @Test(expected = RuntimeDateFormatException.class)
    public void asDateTimeThrowsIfNotCorrectFormat() {
        Header dateHeader = new Header("HELLO: XXX, 09 May 2011 18:49:18 GMT");
        
        dateHeader.asDateTime();
    }
    
}
