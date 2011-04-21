package com.googlecode.rd.serverdriver.http.request;

import com.googlecode.rd.types.Header;

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
