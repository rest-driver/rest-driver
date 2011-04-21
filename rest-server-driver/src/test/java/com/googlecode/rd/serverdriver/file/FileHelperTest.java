package com.googlecode.rd.serverdriver.file;

import org.junit.Test;

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

}
