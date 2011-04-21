package com.github.restdriver.clientdriver.exception;

/**
 * Runtime exception thrown when the client driver cannot start for whatever reason.
 */
public final class ClientDriverInternalException extends RuntimeException {

    private static final long serialVersionUID = -2270688849375416363L;

    /**
     * Constructor
     * 
     * @param message
     *            The message
     * @param cause
     *            The throwable which we caught before throwing this one. Could be null.
     */
    public ClientDriverInternalException(String message, Throwable cause) {
        super(message, cause);
    }

}
