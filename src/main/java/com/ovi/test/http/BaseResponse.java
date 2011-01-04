package com.ovi.test.http;

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

}
