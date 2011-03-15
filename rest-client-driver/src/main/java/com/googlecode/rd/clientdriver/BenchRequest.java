package com.googlecode.rd.clientdriver;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class for encapsulating an expected call to the Http Test Bench {@link BenchServer}.
 * 
 * @author mjg
 * 
 */
public class BenchRequest {

	/**
	 * HTTP method enum for specifying which method you expect to be called with.
	 * 
	 * @author mjg
	 * 
	 */
	public enum Method {
		/** GET */
		GET,

		/** POST */
		POST,

		/** PUT */
		PUT,

		/** DELETE */
		DELETE
	}

	private final Object path;
	private Method method;
	private final Map<String, Object> params;
	private Object bodyContent;
	private Object bodyContentType;

	/**
	 * Constructor taking String.
	 * 
	 * @param path
	 *            The mandatory argument is the path which will be listened on
	 */
	public BenchRequest(final String path) {
		this.path = path;
		this.method = Method.GET;
		this.params = new HashMap<String, Object>();
	}

	/**
	 * Constructor taking Pattern.
	 * 
	 * @param path
	 *            The mandatory argument is the path which will be listened on
	 */
	public BenchRequest(final Pattern path) {
		this.path = path;
		this.method = Method.GET;
		this.params = new HashMap<String, Object>();
	}

	/**
	 * Get the path
	 * 
	 * @return the path which requests are expected on.
	 */
	public Object getPath() {
		return path;
	}

	/**
	 * @param method
	 *            the method to set
	 * @return the object you called the method on, so you can chain these calls.
	 */
	public BenchRequest withMethod(final Method method) {
		this.method = method;
		return this;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * Setter for expecting query-string parameters on the end of the url.
	 * 
	 * @param key
	 *            The key from ?key=value
	 * @param value
	 *            The value from ?key=value in the form of a String
	 * @return the object you called the method on, so you can chain these calls.
	 */
	public BenchRequest withParam(final String key, final String value) {
		params.put(key, value);
		return this;
	}

	/**
	 * Setter for expecting query-string parameters on the end of the url.
	 * 
	 * @param key
	 *            The key from ?key=value
	 * @param value
	 *            The value from ?key=value in the form of a Pattern
	 * @return the object you called the method on, so you can chain these calls.
	 */
	public BenchRequest withParam(final String key, final Pattern value) {
		params.put(key, value);
		return this;
	}

	/**
	 * @return the params
	 */
	public Map<String, Object> getParams() {
		return params;
	}

	/**
	 * toString.
	 * 
	 * @return a String representation of the request
	 */
	@Override
	public String toString() {
		return "BenchRequest: " + method + " " + path.toString() + "; ";
	}

	/**
	 * @return the bodyContent
	 */
	public Object getBodyContent() {
		return bodyContent;
	}

	/**
	 * @return the bodyContentType
	 */
	public Object getBodyContentType() {
		return bodyContentType;
	}

	/**
	 * Setter for expecting body content and type, where content is in the form of a String and type is in the form of a String.
	 * 
	 * @param bodyContent
	 *            the bodyContent to set
	 * @param contentType
	 *            eg "text/plain"
	 * @return the object you called the method on, so you can chain these calls.
	 */
	public BenchRequest withBody(final String bodyContent, final String contentType) {
		this.bodyContent = bodyContent;
		this.bodyContentType = contentType;
		return this;
	}

	/**
	 * Setter for expecting body content and type, where content is in the form of a String and type is in the form of a Pattern.
	 * 
	 * @param bodyContent
	 *            the bodyContent to set
	 * @param contentType
	 *            eg "text/plain"
	 * @return the object you called the method on, so you can chain these calls.
	 */
	public BenchRequest withBody(final String bodyContent, final Pattern contentType) {
		this.bodyContent = bodyContent;
		this.bodyContentType = contentType;
		return this;
	}

	/**
	 * Setter for expecting body content and type, where content is in the form of a Pattern and type is in the form of a String.
	 * 
	 * @param bodyContent
	 *            the bodyContent to set
	 * @param contentType
	 *            eg "text/plain"
	 * @return the object you called the method on, so you can chain these calls.
	 */
	public BenchRequest withBody(final Pattern bodyContent, final String contentType) {
		this.bodyContent = bodyContent;
		this.bodyContentType = contentType;
		return this;
	}

	/**
	 * Setter for expecting body content and type, where content is in the form of a Pattern and type is in the form of a Pattern.
	 * 
	 * @param bodyContent
	 *            the bodyContent to set
	 * @param contentType
	 *            eg "text/plain"
	 * @return the object you called the method on, so you can chain these calls.
	 */
	public BenchRequest withBody(final Pattern bodyContent, final Pattern contentType) {
		this.bodyContent = bodyContent;
		this.bodyContentType = contentType;
		return this;
	}

}
