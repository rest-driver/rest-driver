package com.github.restdriver.serverdriver.http;

import javax.annotation.Generated;

import org.apache.commons.lang.StringUtils;

/**
 * Represents an HTTP header.
 */
public final class Header {

    private final String name;
    private final String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, value);
    }

    @Generated("Eclipse")
    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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

        return StringUtils.equals(name, other.name) && StringUtils.equals(value, other.value);

    }

}
