package com.googlecode.rd.serverdriver.http.request;

import com.googlecode.rd.types.Header;

public class GetRequest extends BaseRequest {

	public GetRequest(String url, Header[] headers) {
		super(url, headers);
	}

	@Override
	public String toString() {
		return "GET " + super.toString();
	}

}
