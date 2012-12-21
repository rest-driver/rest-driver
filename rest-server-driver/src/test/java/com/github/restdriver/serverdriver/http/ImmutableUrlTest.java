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
package com.github.restdriver.serverdriver.http;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ImmutableUrlTest {

    @Test
    public void immutabilityWhenSettingThePath(){
        ImmutableUrl original = new ImmutableUrl("localhost");
        ImmutableUrl withPath = original.withPath("path");

        assertThat(original.toString(), is("http://localhost"));
        assertThat(withPath.toString(), is("http://localhost/path"));
    }

    @Test
    public void immutabilityWhenSettingTheParams(){
        ImmutableUrl original = new ImmutableUrl("localhost");
        ImmutableUrl withPath = original.withParam("k", "v");

        assertThat(original.toString(), is("http://localhost"));
        assertThat(withPath.toString(), is("http://localhost?k=v"));
    }

    // Below are the same tests from UrlTest to validate the behaviour

    @Test
    public void basicImmutableUrlMakesAString() {
        String url = new ImmutableUrl("http://localhost").toString();
        assertThat(url, is("http://localhost"));
    }

    @Test
    public void urlWithPathMakesAString() {
        ImmutableUrl url = new ImmutableUrl("http://localhost").withPath("foo/bar");
        assertThat(url.toString(), is("http://localhost/foo/bar"));
    }

    @Test
    public void urlWithPathMakesAStringWithTrailingSlash() {
        ImmutableUrl url = new ImmutableUrl("http://localhost/").withPath("foo/bar");
        assertThat(url.toString(), is("http://localhost/foo/bar"));
    }

    @Test
    public void urlWithPathMakesAStringWithLeadingSlash() {
        ImmutableUrl url = new ImmutableUrl("http://localhost").withPath("/foo/bar");
        assertThat(url.toString(), is("http://localhost/foo/bar"));
    }

    @Test
    public void multiplePathsAreSlashedCorrectly() {
        ImmutableUrl url = new ImmutableUrl("http://localhost").withPath("a").withPath("/b/").withPath("/c");
        assertThat(url.toString(), is("http://localhost/a/b/c"));
    }

    @Test
    public void endingTrailingSlashIsKept() {
        ImmutableUrl url = new ImmutableUrl("http://localhost").withPath("a").withPath("/b/").withPath("/c/");
        assertThat(url.toString(), is("http://localhost/a/b/c/"));
    }

    @Test
    public void singleParamIsAdded() {
        ImmutableUrl url = new ImmutableUrl("http://localhost").withParam("a", "b");
        assertThat(url.toString(), is("http://localhost?a=b"));
    }

    @Test
    public void multipleParamsAreAdded() {
        ImmutableUrl url = new ImmutableUrl("http://localhost").withParam("a", "b").withParam("c", "d");
        assertThat(url.toString(), is("http://localhost?a=b&c=d"));
    }

    @Test
    public void paramsAndPathsCanBeIntermixed() {
        // not sure how useful this actually is, but we might as well support it.
        ImmutableUrl url = new ImmutableUrl("http://localhost").withParam("a", "b").withPath("c/");
        assertThat(url.toString(), is("http://localhost/c/?a=b"));
    }

    @Test
    public void pathsAreUrlEncoded() {
        ImmutableUrl url = new ImmutableUrl("http://localhost").withPath("   ");
        assertThat(url.toString(), is("http://localhost/%20%20%20"));
    }

    @Test
    public void multiSectionPathsAreUrlEncoded() {
        ImmutableUrl url = new ImmutableUrl("http://localhost").withPath("   /  ");
        assertThat(url.toString(), is("http://localhost/%20%20%20/%20%20"));
    }

    @Test
    public void paramsAreUrlEncoded() {
        ImmutableUrl url = new ImmutableUrl("http://localhost").withParam("a%a", "b>b");
        assertThat(url.toString(), is("http://localhost?a%25a=b%3Eb"));
    }

    @Test
    public void assumesHttpAsScheme() {
        assertThat(new ImmutableUrl("localhost/").toString(), is("http://localhost/"));
    }


}
