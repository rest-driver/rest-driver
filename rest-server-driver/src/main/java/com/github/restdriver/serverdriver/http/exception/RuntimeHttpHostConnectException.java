package com.github.restdriver.serverdriver.http.exception;

import org.apache.http.conn.HttpHostConnectException;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 16:33
 */
public class RuntimeHttpHostConnectException extends RuntimeException {

    private static final long serialVersionUID = -8998669008887196143L;

    public RuntimeHttpHostConnectException(HttpHostConnectException hhce) {
        super(hhce);
    }
}
