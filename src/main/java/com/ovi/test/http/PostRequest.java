package com.ovi.test.http;

public class PostRequest extends BaseContentRequest {

	public PostRequest(final String url, final String content, final Header[] headers) {
		super(url, content, headers);
	}

	@Override
	public String toString() {
		return "POST " + super.toString();
	}

}
