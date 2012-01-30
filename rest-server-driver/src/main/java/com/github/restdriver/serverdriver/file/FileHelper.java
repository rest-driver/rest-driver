/**
 * Copyright © 2010-2011 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.restdriver.serverdriver.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * Utility class to help with loading files from the classpath.
 * 
 * User: mjg
 * Date: 21/04/11
 * Time: 11:51
 */
public final class FileHelper {
    
    private FileHelper() {
    }
    
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    /**
     * Reads in a resource from the class path.
     * 
     * @param fileName The file name to load
     * @param encoding The encoding to use when reading the file
     * @return The content of the file
     */
    public static String fromFile(String fileName, String encoding) {
        
        InputStream stream = FileHelper.class.getClassLoader().getResourceAsStream(fileName);
        
        if (stream == null) {
            throw new RuntimeFileNotFoundException(new FileNotFoundException(fileName));
            
        }
        
        try {
            return IOUtils.toString(stream, encoding);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from file " + fileName, e);
        }
    }
    
    /**
     * Reads in a resource from the class path, using UTF-8 encoding.
     * 
     * @param fileName The file name to load
     * @return The content of the file
     */
    public static String fromFile(String fileName) {
        return fromFile(fileName, DEFAULT_ENCODING);
    }
    
    /**
     * Reads in a resource from the class path and allow parameters in the
     * file to substituted with other strings. Uses UTF-8 encoding.
     * 
     * @param filename The name of the file to load
     * @return A builder to create the file
     */
    public static FileWithParametersBuilder fromFileWithParameters(String filename) {
        return fromFileWithParameters(filename, DEFAULT_ENCODING);
    }
    
    /**
     * Reads in a resource from the class path and allow parameters in the
     * file to substituted with other strings. Uses UTF-8 encoding.
     * 
     * @param filename The name of the file to load
     * @param encoding The encoding to use when reading the file.
     * @return A builder to create the file
     */
    public static FileWithParametersBuilder fromFileWithParameters(String filename, String encoding) {
        return new FileWithParametersBuilder(fromFile(filename, encoding));
    }
    
    /**
     * Class to help substitute parameters in a file.
     */
    public static class FileWithParametersBuilder {
        
        private final String contents;
        
        public FileWithParametersBuilder(String contents) {
            this.contents = contents;
        }
        
        /**
         * Replace all instances of {{name}} in the file with value.
         * 
         * @param name Key name to replace
         * @param value New value
         * @return A new builder that has name substituted for value
         */
        public FileWithParametersBuilder withParameter(String name, String value) {
            return new FileWithParametersBuilder(substitute(name, value));
        }
        
        /**
         * For all entries in parameters replace instances of the {{key}} with value
         * 
         * @param parameters A map of parameter replacements
         * @return A new builder that has all the substitutions completed.
         */
        public FileWithParametersBuilder withParameters(Map<String, String> parameters) {
            
            FileWithParametersBuilder builder = this;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                builder = builder.withParameter(entry.getKey(), entry.getValue());
            }
            
            return builder;
        }
        
        private String substitute(String name, String value) {
            return contents.replace("{{" + name + "}}", value);
        }
        
        @Override
        public String toString() {
            return contents;
        }
        
    }
    
}
