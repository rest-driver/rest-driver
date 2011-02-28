package com.googlecode.rat.http;

public class DeleteRequest extends BaseRequest {

	public DeleteRequest(final String url, final Header[] headers) {
		super(url, headers);
	}

	@Override
	public String toString() {
		return "DELETE " + super.toString();
	}

}
