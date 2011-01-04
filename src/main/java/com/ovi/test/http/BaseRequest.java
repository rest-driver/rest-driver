package com.ovi.test.http;

public abstract class BaseRequest implements Request {

	private final String url;
	private final Header[] headers;

	/**
	 * Constructor
	 * 
	 * @param url
	 *            The URL
	 * @param headers
	 *            The headers for the request
	 */
	public BaseRequest(final String url, final Header[] headers) {
		this.url = url;
		this.headers = headers;
	}

	@Override
	public final String getUrl() {
		return url;
	}

	@Override
	public final Header[] getHeaders() {
		return headers;
	}

}
