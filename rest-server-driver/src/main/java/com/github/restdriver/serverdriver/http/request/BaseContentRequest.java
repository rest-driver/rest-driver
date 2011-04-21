package com.github.restdriver.serverdriver.http.request;

import com.github.restdriver.types.Header;

public abstract class BaseContentRequest extends BaseRequest implements ContentRequest {

	private  String content;

	public BaseContentRequest( String url,  String content,  Header[] headers) {
		super(url, headers);
		this.content = content;
	}

	@Override
	public  String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "content=" + content + "," + super.toString();
	}

}
