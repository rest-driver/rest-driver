package com.googlecode.rat.http;

import static org.apache.commons.lang.StringUtils.*;

public class DefaultResponse implements Response {

	private final int statusCode;
	private final String content;
	private final Header[] headers;
	private final long responseTime;

	public DefaultResponse(final int statusCode, final String content, final Header[] headers, final long responseTime) {
		this.statusCode = statusCode;
		this.content = content;
		this.headers = headers;
		this.responseTime = responseTime;
	}

	@Override
	public final int getStatusCode() {
		return statusCode;
	}

	@Override
	public final String getContent() {
		return content;
	}

	@Override
	public final Header[] getHeaders() {
		return headers;
	}

	@Override
	public final long getResponseTime() {
		return responseTime;
	}

	@Override
	public String toString() {
		return "status=" + statusCode + "|content=" + content + "|headers=[" + join(headers, ",") + "]";
	}

}
