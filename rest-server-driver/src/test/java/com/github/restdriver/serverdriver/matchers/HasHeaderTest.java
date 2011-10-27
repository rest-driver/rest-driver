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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

public class HasHeaderTest {
    
    private HasHeader matcher;
    
    @Before
    public void before() {
        matcher = new HasHeader("Header");
    }
    
    @Test
    public void matcherShouldDescribeItselfCorrectly() {
        Description description = new StringDescription();
        matcher.describeTo(description);
        
        assertThat(description.toString(), is("Response with header named 'Header'"));
    }
    
    @Test
    public void matcherShouldFailWhenResponseHasNoHeaders() {
        Response mockResponse = mock(Response.class);
        when(mockResponse.getHeaders()).thenReturn(new ArrayList<Header>());
        
        assertThat(matcher.matches(mockResponse), is(false));
    }
    
    @Test
    public void matcherShouldMatchHeaderCaseInsensitively() {
        List<Header> headers = Arrays.asList(new Header("header: value"));
        Response mockResponse = mock(Response.class);
        when(mockResponse.getHeaders()).thenReturn(headers);
        
        assertThat(matcher.matches(mockResponse), is(true));
    }
    
    @Test
    public void matcherShouldDescribeMismatchCorrectlyWithNoHeaders() {
        Description description = new StringDescription();
        Response mockResponse = mock(Response.class);
        matcher.describeMismatchSafely(mockResponse, description);
        
        assertThat(description.toString(), is("Response has no headers"));
    }
    
    @Test
    public void matcherShouldDescribeMismatchCorrectlyWithHeaders() {
        List<Header> headers = Arrays.asList(new Header("This: that"), new Header("The: other"));
        Description description = new StringDescription();
        Response mockResponse = mock(Response.class);
        when(mockResponse.getHeaders()).thenReturn(headers);
        matcher.describeMismatchSafely(mockResponse, description);
        
        assertThat(description.toString(), is("Response has headers [This: that, The: other]"));
    }
    
}
