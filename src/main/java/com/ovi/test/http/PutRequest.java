package com.ovi.test.http;

public class PutRequest extends BaseContentRequest {

	public PutRequest(final String url, final String content, final Header[] headers) {
		super(url, content, headers);
	}

	@Override
	public String toString() {
		return "PUT " + super.toString();
	}

}
