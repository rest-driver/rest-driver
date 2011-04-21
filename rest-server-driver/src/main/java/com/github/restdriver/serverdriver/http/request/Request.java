package com.github.restdriver.serverdriver.http.request;

import com.github.restdriver.serverdriver.http.Header;

public interface Request {

	String getUrl();

	Header[] getHeaders();

}
