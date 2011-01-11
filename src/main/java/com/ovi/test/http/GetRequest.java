package com.ovi.test.http;

public class GetRequest extends BaseRequest {

	public GetRequest(final String url) {
		super(url, new Header[0]);
	}

	@Override
	public String toString() {
		return "GET " + super.toString();
	}

}
