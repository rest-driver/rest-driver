package com.googlecode.rat.http.request;

import com.googlecode.rat.http.Header;

public class GetRequest extends BaseRequest {

	public GetRequest(final String url, final Header[] headers) {
		super(url, headers);
	}

	@Override
	public String toString() {
		return "GET " + super.toString();
	}

}
