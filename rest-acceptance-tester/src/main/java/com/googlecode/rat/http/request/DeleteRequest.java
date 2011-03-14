package com.googlecode.rat.http.request;

import com.googlecode.rat.http.Header;

public class DeleteRequest extends BaseRequest {

	public DeleteRequest(final String url, final Header[] headers) {
		super(url, headers);
	}

	@Override
	public String toString() {
		return "DELETE " + super.toString();
	}

}
