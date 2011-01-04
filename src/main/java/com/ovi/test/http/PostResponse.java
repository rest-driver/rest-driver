package com.ovi.test.http;

public class PostResponse extends BaseResponse {

	private final String content;

	public PostResponse(final int statusCode, final String content, final Header[] headers) {
		super(statusCode, headers);
		this.content = content;
	}

	@Override
	public final String getContent() {
		return content;
	}

}
