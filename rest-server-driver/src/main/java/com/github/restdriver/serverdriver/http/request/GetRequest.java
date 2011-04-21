package com.github.restdriver.serverdriver.http.request;

import com.github.restdriver.types.Header;

public class GetRequest extends BaseRequest {

	public GetRequest(String url, Header[] headers) {
		super(url, headers);
	}

	@Override
	public String toString() {
		return "GET " + super.toString();
	}

}
