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
import org.junit.Test;

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
        assertThat(header.toString(), is("name:value"));
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
        Header header = new Header("  X-foo  :   blah  ");
        assertThat(header.getName(), is("X-foo"));
        assertThat(header.getValue(), is("blah"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void headerWithSingleStringAndNoColonIsIllegal() {
        new Header("  X-foo ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void headerWithSingleStringAndTwoColonsIsIllegal() {
        new Header("  X-foo : yes : perhaps");
    }

    public void headerAppliesItselfToRequest() {
        HttpUriRequest request = new HttpGet();
        Header header = new Header("name", "value");
        header.applyTo(request);
        assertThat(request.getFirstHeader("name").getValue(), is("value"));
    }

}
