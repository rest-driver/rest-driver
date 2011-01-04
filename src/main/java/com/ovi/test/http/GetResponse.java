package com.ovi.test.http;

public class GetResponse extends BaseResponse {

	private final String content;

	public GetResponse(final int statusCode, final String content, final Header[] headers) {
		super(statusCode, headers);
		this.content = content;
	}

	@Override
	public final String getContent() {
		return content;
	}

}
