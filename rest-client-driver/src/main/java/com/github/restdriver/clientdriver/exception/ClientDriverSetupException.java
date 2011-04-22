package com.github.restdriver.clientdriver.exception;

/**
 * Runtime exception which is thrown for a variety of causes from the Http Test Bench. This exception is thrown within
 * the HTTP server itself, so will not cause tests to fail, but will appear in the server logs (usually to STDERR)
 */
public class ClientDriverSetupException extends RuntimeException {

    private static final long serialVersionUID = -2270688849375416363L;

    /**
     * Constructor.
     * 
     * @param message
     *            The message
     * @param cause
     *            The throwable which we caught before throwing this one. Could be null.
     */
    public ClientDriverSetupException(String message, Throwable cause) {
        super(message, cause);
    }

}
