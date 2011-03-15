package com.googlecode.rd.serverdriver.http.request;

import static org.apache.commons.lang.StringUtils.*;

import com.googlecode.rd.serverdriver.http.Header;

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

	@Override
	public String toString() {
		return "url=" + url + ",headers=[" + join(headers, ",") + "]";
	}

}
