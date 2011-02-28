package com.nokia.batchprocessor.testbench;

/**
 * Runtime exception which is thrown for a variety of causes from the Http Test
 * Bench. This exception is thrown within the HTTP server itself, so will not
 * cause tests to fail, but will appear in the server logs (usually to STDERR)
 * 
 * @author mjg
 * 
 */
public class BenchServerRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -2270688849375416363L;

    /**
     * Constructor
     * 
     * @param message
     *            The message
     * @param cause
     *            The throwable which we caught before throwing this one. Could
     *            be null.
     */
    public BenchServerRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
