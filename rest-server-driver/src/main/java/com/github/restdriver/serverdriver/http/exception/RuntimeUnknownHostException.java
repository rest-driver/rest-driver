package com.github.restdriver.serverdriver.http.exception;

import java.net.UnknownHostException;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 16:15
 *
 * RuntimeException wrapper for java.net.UnknownHostException
 */
public final class RuntimeUnknownHostException extends RuntimeException{

    public RuntimeUnknownHostException(UnknownHostException e){
        super(e);
    }

}
