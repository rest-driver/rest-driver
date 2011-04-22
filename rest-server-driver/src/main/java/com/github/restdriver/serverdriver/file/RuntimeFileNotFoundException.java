package com.github.restdriver.serverdriver.file;

import java.io.FileNotFoundException;

/**
 * Runtime wrapper for {@link FileNotFoundException}.
 * 
 * User: mjg
 * Date: 21/04/11
 * Time: 12:08
 */
public class RuntimeFileNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3651887477900227630L;

    public RuntimeFileNotFoundException(FileNotFoundException fnfe) {
        super(fnfe);
    }

}
