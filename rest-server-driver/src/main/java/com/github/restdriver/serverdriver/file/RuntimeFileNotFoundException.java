package com.github.restdriver.serverdriver.file;

import java.io.FileNotFoundException;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 12:08
 */
public class RuntimeFileNotFoundException extends RuntimeException {

    public RuntimeFileNotFoundException(FileNotFoundException fnfe) {
        super(fnfe);
    }

}
