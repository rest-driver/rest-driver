package com.googlecode.rd.serverdriver.http.request;

import com.googlecode.rd.types.Header;

public class PutRequest extends BaseContentRequest {

	public PutRequest(final String url, final String content, final Header[] headers) {
		super(url, content, headers);
	}

	@Override
	public String toString() {
		return "PUT " + super.toString();
	}

}
