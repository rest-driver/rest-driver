package com.googlecode.rd.serverdriver.http.request;

import com.googlecode.rd.types.Header;

public class GetRequest extends BaseRequest {

	public GetRequest(final String url, final Header[] headers) {
		super(url, headers);
	}

	@Override
	public String toString() {
		return "GET " + super.toString();
	}

}
