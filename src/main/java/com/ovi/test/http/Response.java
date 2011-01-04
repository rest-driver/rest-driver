package com.ovi.test.http;

public interface Response {

	int getStatusCode();

	String getContent();

	Header[] getHeaders();

}
