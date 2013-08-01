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
package com.github.restdriver.matchers;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * User: mjg
 * Date: 07/05/11
 * Time: 22:21
 */
public class HasJsonValueTest {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    private HasJsonValue hasJsonValue;
    
    @Test
    public void jsonMatchesString() {
        JsonNode json = asJson("{\"foo\": \"bar\"}");
        hasJsonValue = new HasJsonValue("foo", is("bar"));
        assertThat(hasJsonValue.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonFailsNonMatchingString() {
        JsonNode json = asJson("{\"foo\": \"grap\"}");
        hasJsonValue = new HasJsonValue("foo", is("bar"));
        assertThat(hasJsonValue.matchesSafely(json), is(false));
    }
    
    @Test
    public void jsonMatchesANumber() {
        JsonNode json = asJson("{\"foo\": 5}");
        hasJsonValue = new HasJsonValue("foo", greaterThan(4));
        assertThat(hasJsonValue.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonFailsNonMatchingNumber() {
        JsonNode json = asJson("{\"foo\": 5}");
        hasJsonValue = new HasJsonValue("foo", greaterThan(6));
        assertThat(hasJsonValue.matchesSafely(json), is(false));
    }
    
    @Test
    public void jsonMatchesABool() {
        JsonNode json = asJson("{\"foo\": true}");
        hasJsonValue = new HasJsonValue("foo", is(true));
        assertThat(hasJsonValue.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonFailsNonMatchingBool() {
        JsonNode json = asJson("{\"foo\": true}");
        hasJsonValue = new HasJsonValue("foo", is(not(true)));
        assertThat(hasJsonValue.matchesSafely(json), is(false));
    }
    
    @Test
    public void jsonMatchesADouble() {
        JsonNode json = asJson("{\"foo\": 123.456}");
        hasJsonValue = new HasJsonValue("foo", is(123.456));
        assertThat(hasJsonValue.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonFailsNonMatchingDouble() {
        JsonNode json = asJson("{\"foo\": 987.654}");
        // hasJsonValue = new HasJsonValue("foo", greaterThan(1000)); -- ClassCastException
        hasJsonValue = new HasJsonValue("foo", greaterThan(1000.0));
        assertThat(hasJsonValue.matchesSafely(json), is(false));
    }
    
    @Test
    public void jsonMatchesANull() {
        JsonNode json = asJson("{\"foo\": null}");
        hasJsonValue = new HasJsonValue("foo", nullValue());
        assertThat(hasJsonValue.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonFailsNonMatchingNull() {
        JsonNode json = asJson("{\"foo\": true}");
        hasJsonValue = new HasJsonValue("foo", is(nullValue()));
        assertThat(hasJsonValue.matchesSafely(json), is(false));
    }
    
    @Test
    public void jsonMatchesABase64String() {
        
        String base64Data =
                "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz" +
                        "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg" +
                        "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu" +
                        "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo" +
                        "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
        
        JsonNode json = asJson("{\"foo\": \"" + base64Data + "\"}");
        
        hasJsonValue = new HasJsonValue("foo", is(base64Data));
        assertThat(hasJsonValue.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonMatchesALong() {
        JsonNode json = asJson("{\"foo\": 123456789012345}");
        hasJsonValue = new HasJsonValue("foo", is(123456789012345L));
        assertThat(hasJsonValue.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonFailsNonMatchingLong() {
        JsonNode json = asJson("{\"foo\": 123456789012345}");
        hasJsonValue = new HasJsonValue("foo", lessThan(12345678L));
        assertThat(hasJsonValue.matchesSafely(json), is(false));
    }
    
    @Test
    public void jsonSuccessfullyMatchesNestedObject() {
        JsonNode json = asJson("{\"address\": { \"postcode\": \"BS1 2PH\" } }");
        hasJsonValue = new HasJsonValue("address", new HasJsonValue("postcode", is("BS1 2PH")));
        assertThat(hasJsonValue.matchesSafely(json), is(true));
    }
    
    @Test
    public void jsonFailsMatchOnNestedObject() {
        JsonNode json = asJson("{\"address\": { \"postcode\": \"BS1 2PH\" } }");
        hasJsonValue = new HasJsonValue("address", new HasJsonValue("postcode", is("wrong")));
        assertThat(hasJsonValue.matchesSafely(json), is(false));
    }
    
    private static JsonNode asJson(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
