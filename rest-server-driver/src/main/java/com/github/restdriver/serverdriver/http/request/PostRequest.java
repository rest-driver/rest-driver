package com.github.restdriver.serverdriver.http.request;

import com.github.restdriver.types.Header;

public class PostRequest extends BaseContentRequest {

	public PostRequest(final String url, final String content, final Header[] headers) {
		super(url, content, headers);
	}

	@Override
	public String toString() {
		return "POST " + super.toString();
	}

}
