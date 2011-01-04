package com.ovi.test.http;

public class DeleteResponse extends BaseResponse {

	public DeleteResponse(final int statusCode, final Header[] headers) {
		super(statusCode, headers);
	}

	@Override
	public final String getContent() {
		return null;
	}

}
