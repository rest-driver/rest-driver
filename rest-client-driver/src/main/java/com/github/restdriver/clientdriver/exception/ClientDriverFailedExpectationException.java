package com.github.restdriver.clientdriver.exception;

/**
 * Runtime exception which is thrown when the client driver's expectations fail.
 */
public final class ClientDriverFailedExpectationException extends RuntimeException {

    private static final long serialVersionUID = -2270688849375416363L;

    /**
     * Constructor.
     * 
     * @param message
     *            The message
     * @param cause
     *            The throwable which we caught before throwing this one. Could be null.
     */
    public ClientDriverFailedExpectationException(String message, Throwable cause) {
        super(message, cause);
    }

}
