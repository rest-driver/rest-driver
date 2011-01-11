package com.ovi.test.http;

public abstract class BaseContentRequest extends BaseRequest implements ContentRequest {

	private final String content;

	public BaseContentRequest(final String url, final String content, final Header[] headers) {
		super(url, headers);
		this.content = content;
	}

	@Override
	public final String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "content=" + content + "," + super.toString();
	}

}
