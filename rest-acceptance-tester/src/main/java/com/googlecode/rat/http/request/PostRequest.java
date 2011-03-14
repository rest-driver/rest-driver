package com.googlecode.rat.http.request;

import com.googlecode.rat.http.Header;

public class PostRequest extends BaseContentRequest {

	public PostRequest(final String url, final String content, final Header[] headers) {
		super(url, content, headers);
	}

	@Override
	public String toString() {
		return "POST " + super.toString();
	}

}
