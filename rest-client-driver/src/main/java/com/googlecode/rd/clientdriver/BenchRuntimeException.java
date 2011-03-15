package com.googlecode.rd.clientdriver;

/**
 * Runtime exception which is thrown for a variety of causes from the Http Test Bench. This is runtime so you don't have to bother catching it, but it
 * will fail your tests if it is thrown.
 * 
 * @author mjg
 * 
 */
public class BenchRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -2270688849375416363L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 * @param cause
	 *            The throwable which we caught before throwing this one. Could be null.
	 */
	public BenchRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
