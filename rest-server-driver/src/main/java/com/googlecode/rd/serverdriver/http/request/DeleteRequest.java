package com.googlecode.rd.serverdriver.http.request;

import com.googlecode.rd.types.Header;

public class DeleteRequest extends BaseRequest {

	public DeleteRequest(final String url, final Header[] headers) {
		super(url, headers);
	}

	@Override
	public String toString() {
		return "DELETE " + super.toString();
	}

}
