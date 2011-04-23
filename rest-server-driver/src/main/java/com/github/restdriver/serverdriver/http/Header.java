package com.github.restdriver.serverdriver.http;

import javax.annotation.Generated;

import org.apache.commons.lang.StringUtils;

/**
 * Represents an HTTP header.
 */
public final class Header {

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
