package com.ovi.test.http;

public abstract class BaseContentRequest extends BaseRequest implements ContentRequest {

	private final String content;

	public BaseContentRequest(final String url, final String content, final Header[] headers) {
		super(url, headers);
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}

}
