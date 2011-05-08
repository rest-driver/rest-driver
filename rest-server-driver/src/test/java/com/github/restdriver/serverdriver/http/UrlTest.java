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

import com.github.restdriver.serverdriver.http.exception.RuntimeUriSyntaxException;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * User: mjg
 * Date: 07/05/11
 * Time: 23:44
 */
public class UrlTest {

    @Test
    public void basicUrlMakesAString(){
        String url = new Url("http://localhost").toString();
        assertThat( url, is("http://localhost") );
    }

    @Test
    public void urlWithPathMakesAString(){
        Url url = new Url("http://localhost").withPath("foo/bar");
        assertThat( url.toString(), is("http://localhost/foo/bar") );
    }

    @Test
    public void urlWithPathMakesAStringWithTrailingSlash(){
        Url url = new Url("http://localhost/").withPath("foo/bar");
        assertThat( url.toString(), is("http://localhost/foo/bar") );
    }

    @Test
    public void urlWithPathMakesAStringWithLeadingSlash(){
        Url url = new Url("http://localhost").withPath("/foo/bar");
        assertThat( url.toString(), is("http://localhost/foo/bar") );
    }

    @Test
    public void multiplePathsAreSlashedCorrectly(){
        Url url = new Url("http://localhost").withPath("a").withPath("/b/").withPath("/c");
        assertThat( url.toString(), is("http://localhost/a/b/c") );
    }

    @Test
    public void endingTrailingSlashIsKept(){
        Url url = new Url("http://localhost").withPath("a").withPath("/b/").withPath("/c/");
        assertThat( url.toString(), is("http://localhost/a/b/c/") );
    }

    @Test
    public void singleParamIsAdded(){
        Url url = new Url("http://localhost").withParam("a", "b");
        assertThat( url.toString(), is("http://localhost?a=b") );
    }

    @Test
    public void multipleParamsAreAdded(){
        Url url = new Url("http://localhost").withParam("a", "b").withParam("c", "d");
        assertThat( url.toString(), is("http://localhost?a=b&c=d") );
    }

    @Test
    public void paramsAndPathsCanBeIntermixed(){
        // not sure how useful this actually is, but we might as well support it.
        Url url = new Url("http://localhost").withParam("a", "b").withPath("c/");
        assertThat( url.toString(), is("http://localhost/c/?a=b") );
    }

    @Test
    public void pathsAreUrlEncoded(){
        Url url = new Url("http://localhost").withPath("   ");
        assertThat( url.toString(), is("http://localhost/%20%20%20") );
    }

    @Test
    public void multiSectionPathsAreUrlEncoded(){
        Url url = new Url("http://localhost").withPath("   /  ");
        assertThat( url.toString(), is("http://localhost/%20%20%20/%20%20") );
    }

    @Test
    public void paramsAreUrlEncoded(){
        Url url = new Url("http://localhost").withParam("a%a", "b>b");
        assertThat( url.toString(), is("http://localhost?a%25a=b%3Eb") );
    }

    @Test
    public void assumesHttpAsScheme(){
        assertThat( new Url("localhost/").toString(), is("http://localhost/"));
    }

}
