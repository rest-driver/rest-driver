package com.googlecode.rd.serverdriver.http.request;

import com.googlecode.rd.types.Header;

public interface Request {

	String getUrl();

	Header[] getHeaders();

}
