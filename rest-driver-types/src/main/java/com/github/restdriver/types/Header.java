package com.github.restdriver.types;

import org.apache.commons.lang.StringUtils;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Header)) {
			return false;
		}

		final Header other = (Header) object;

		return StringUtils.equals(name, other.name) && StringUtils.equals(value, other.value);

	}
	
}
