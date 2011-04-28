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
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Represents an HTTP header.
 */
public final class Header implements RequestModifier {

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
    public void applyTo(HttpUriRequest request) {
        request.addHeader(name, value);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, value);
    }

    @Generated("Eclipse")
    @Override
    // CHECKSTYLE:OFF
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    // CHECKSTYLE:ON

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

        return StringUtils.equals(name, other.name) && StringUtils.equals(value, other.value);

    }

}
