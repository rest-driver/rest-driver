package com.ovi.test.http;

public class PutResponse extends BaseResponse {

	private final String content;

	public PutResponse(final int statusCode, final String content, final Header[] headers) {
		super(statusCode, headers);
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}

}
