package com.github.restdriver.serverdriver.http.exception;

import java.net.UnknownHostException;

/**
 * RuntimeException wrapper for {@link UnknownHostException}.
 * 
 * User: mjg
 * Date: 21/04/11
 * Time: 16:15
 */
public final class RuntimeUnknownHostException extends RuntimeException {

    private static final long serialVersionUID = -3385006541772137637L;

    /**
     * Create a new instance with a specific cause.
     * 
     * @param e The exception to use as the cause
     */
    public RuntimeUnknownHostException(UnknownHostException e) {
        super(e);
    }

}
