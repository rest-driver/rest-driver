package com.googlecode.rat.http;

public interface Response {

	int getStatusCode();

	String getContent();

	Header[] getHeaders();

	long getResponseTime();

}
