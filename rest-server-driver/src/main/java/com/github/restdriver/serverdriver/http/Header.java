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

import javax.annotation.Generated;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.github.restdriver.serverdriver.matchers.Rfc1123DateMatcher;

/**
 * Represents an HTTP header.
 */
public final class Header implements AnyRequestModifier {
    
    private static final int HASH_CODE_PRIME = 31;
    
    private final String name;
    private final String value;
    
    /**
     * Creates a new header instance.
     * 
     * @param name The header name
     * @param value The header value
     */
    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    /**
     * Creates a new header instance.
     * 
     * @param nameAndValue The name and value as "name: value".
     */
    public Header(String nameAndValue) {
        
        String[] parts = nameAndValue.split(":", 2);
        
        if (parts.length != 2) {
            throw new IllegalArgumentException("Single-argument Header must be 'name: value'");
        }
        
        name = StringUtils.trim(parts[0]);
        value = StringUtils.trim(parts[1]);
    }
    
    /**
     * Get the name of this header.
     * 
     * @return The header name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the value of this header.
     * 
     * @return The header value
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public void applyTo(ServerDriverHttpUriRequest request) {
        request.getHttpUriRequest().addHeader(name, value);
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s", name, value);
    }
    
    /**
     * The value of this header as a JodaTime {@link DateTime} in UTC. If not valid, a RuntimeDateFormatException will be thrown.
     * 
     * @return The DateTime object..
     */
    public DateTime asDateTime() {
        return new Rfc1123DateMatcher().getDateTime(this.getValue());
    }
    
    @Generated("Eclipse")
    @Override
    public int hashCode() {
        int prime = HASH_CODE_PRIME;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }
    
    @Generated("Eclipse")
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        
        if (!(object instanceof Header)) {
            return false;
        }
        
        Header other = (Header) object;
        
        return StringUtils.equalsIgnoreCase(name, other.name) && StringUtils.equals(value, other.value);
        
    }
    
}
