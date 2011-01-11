package com.ovi.test.http;

import static org.apache.commons.lang.StringUtils.*;

public abstract class BaseResponse implements Response {

	private final int statusCode;
	private final Header[] headers;

	public BaseResponse(final int statusCode, final Header[] headers) {
		this.statusCode = statusCode;
		this.headers = headers;
	}

	@Override
	public final int getStatusCode() {
		return statusCode;
	}

	@Override
	public final Header[] getHeaders() {
		return headers;
	}

	@Override
	public String toString() {
		return "status=" + statusCode + ",headers=[" + join(headers, ",") + "]";
	}

}
