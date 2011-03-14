package com.googlecode.rat.http.request;

import com.googlecode.rat.http.Header;

public class PutRequest extends BaseContentRequest {

	public PutRequest(final String url, final String content, final Header[] headers) {
		super(url, content, headers);
	}

	@Override
	public String toString() {
		return "PUT " + super.toString();
	}

}
