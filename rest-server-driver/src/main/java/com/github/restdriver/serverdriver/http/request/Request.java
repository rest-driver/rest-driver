package com.github.restdriver.serverdriver.http.request;

import com.github.restdriver.types.Header;

public interface Request {

	String getUrl();

	Header[] getHeaders();

}
