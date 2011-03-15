package com.googlecode.rd.serverdriver.http.request;

import com.googlecode.rd.serverdriver.http.Header;

public interface Request {

	String getUrl();

	Header[] getHeaders();

}
