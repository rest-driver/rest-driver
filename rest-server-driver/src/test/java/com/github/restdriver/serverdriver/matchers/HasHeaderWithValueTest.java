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

import java.util.Arrays;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

public class HasHeaderWithValueTest {
    
    private HasHeaderWithValue matcher;
    
    @Before
    public void before() {
        matcher = new HasHeaderWithValue("Content-Type", containsString("application/json"));
    }
    
    @Test
    public void matcherShouldDescribeItselfCorrectly() {
        Description description = new StringDescription();
        matcher.describeTo(description);
        
        assertThat(description.toString(), is("Response with header named 'Content-Type' and value matching: a string containing \"application/json\""));
    }
    
    @Test
    public void matcherShouldFailIfResponseDoesntHaveHeaders() {
        Response mockResponse = mock(Response.class);
        
        assertThat(matcher.matches(mockResponse), is(false));
    }
    
    @Test
    public void matcherShouldFailIfMatcherDoesntMatch() {
        Response mockResponse = mock(Response.class);
        when(mockResponse.getHeaders()).thenReturn(Arrays.asList(new Header("Content-Type", "text/xml")));
        
        assertThat(matcher.matches(mockResponse), is(false));
    }
    
    @Test
    public void matcherShouldPassIfMatcherMatches() {
        Response mockResponse = mock(Response.class);
        when(mockResponse.getHeaders()).thenReturn(Arrays.asList(new Header("Content-Type", "application/json;charset=UTF-8")));
        
        assertThat(matcher.matches(mockResponse), is(true));
    }
    
    @Test
    public void matcherShouldDescribeMismatchCorrectlyIfResponseHasNoHeaders() {
        Response mockResponse = mock(Response.class);
        Description description = new StringDescription();
        matcher.describeMismatchSafely(mockResponse, description);
        
        assertThat(description.toString(), is("Response has no headers"));
    }
    
    @Test
    public void matcherShouldDescribeMismatchCorrectlyIfResponseHasHeaders() {
        Response mockResponse = mock(Response.class);
        when(mockResponse.getHeaders()).thenReturn(Arrays.asList(new Header("Header1: value"), new Header("Header2: value")));
        Description description = new StringDescription();
        matcher.describeMismatchSafely(mockResponse, description);
        
        assertThat(description.toString(), is("Response has headers [Header1: value, Header2: value]"));
    }
    
}
