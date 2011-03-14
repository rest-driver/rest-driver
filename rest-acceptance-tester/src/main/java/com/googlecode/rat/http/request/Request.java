package com.googlecode.rat.http.request;

import com.googlecode.rat.http.Header;

public interface Request {

	String getUrl();

	Header[] getHeaders();

}
