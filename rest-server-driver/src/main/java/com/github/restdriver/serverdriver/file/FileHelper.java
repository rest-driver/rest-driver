package com.github.restdriver.serverdriver.file;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 11:51
 */

public class FileHelper {

    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Reads in a resource from the class path
     *
     * @param fileName The file name to load
     * @param encoding The encoding to use when reading the file
     * @return The content of the file
     */
    public static String fromFile(String fileName, String encoding) {

        InputStream stream = FileHelper.class.getClassLoader().getResourceAsStream(fileName);

        if (stream == null) {
            throw new RuntimeFileNotFoundException("Couldn't find file " + fileName);
            
        }

        try {
            return IOUtils.toString(stream, encoding);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read from file " + fileName, e) ;
        }
    }

    /**
     * Reads in a resource from the class path, using UTF-8 encoding
     *
     * @param fileName The file name to load
     * @return The content of the file
     */
    public static String fromFile(String fileName) {
        return fromFile(fileName, DEFAULT_ENCODING);
    }


}
