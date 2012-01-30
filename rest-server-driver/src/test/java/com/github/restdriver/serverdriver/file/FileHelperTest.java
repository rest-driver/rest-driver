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

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 11:51
 */
public class FileHelperTest {
    
    @Test
    public void readFileFromClasspath() {
        String fileContent = FileHelper.fromFile("english.txt");
        assertThat(fileContent, is("HELLO THIS IS ENGLISH"));
    }
    
    @Test
    public void readUtf8FileFromClasspath() {
        String fileContent = FileHelper.fromFile("japanese.txt");
        assertThat(fileContent, is("こんにちは"));
    }
    
    @Test
    public void readUtf8FileFromClasspathWithIncorrectEncoding() {
        String fileContent = FileHelper.fromFile("japanese.txt", "ASCII");
        assertThat(fileContent, not(is("こんにちは")));
    }
    
    @Test(expected = RuntimeFileNotFoundException.class)
    public void readMissingFileFromClasspathThrowsException() {
        FileHelper.fromFile("missing.txt");
    }
    
    @Test
    public void replaceParameter() {
        String fileContent = FileHelper.fromFileWithParameters("parameter.txt").withParameter("name", "Andrew").toString();
        assertThat(fileContent, is("Hello Andrew\nGoodbye Andrew"));
    }
    
    @Test
    public void replaceParameterInAscii() {
        String fileContent = FileHelper.fromFileWithParameters("parameter.txt", "ASCII").withParameter("name", "Andrew").toString();
        assertThat(fileContent, is("Hello Andrew\nGoodbye Andrew"));
    }
    
    @Test
    public void replaceParameters() {
        Map<String, String> parameters = new HashMap<String, String>() {
            {
                put("greeting", "Hello");
                put("thing", "world");
            }
        };
        String fileContent = FileHelper.fromFileWithParameters("parameters.txt").withParameters(parameters).toString();
        assertThat(fileContent, is("Hello, world!"));
    }
    
}
