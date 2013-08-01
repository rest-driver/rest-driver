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

/**
 * Immutable version of the {@link Url} class.  Methods do not modify the url but return new modified copies.
 * @author mjg
 * 21/12/2012
 */
public final class ImmutableUrl {

    private final Url mutableUrl;

    /**
     * Setup an ImmutableUrl with a base path, like "http://localhost:8080". You can also supply just "localhost" and the
     * "http" will be inferred.
     *
     * @param base the base Url.
     */
    public ImmutableUrl(String base){
        mutableUrl = new Url(base);
    }

    /**
     * Private constructor to be called by "modifying" methods.  This is private so we don't expose our dependency
     * on the Url class.
     */
    private ImmutableUrl(Url url){
        this.mutableUrl = url;
    }

    /**
     * Add a path to a url. This method ensures that there is always exactly one "/" character between segments (so you don't have to :).
     *
     * @param path the path, eg "foo/bar"
     * @return The new ImmutableUrl object (for chaining calls)
     */
    public ImmutableUrl withPath(String path) {
        Url newMutableUrl = new Url(mutableUrl).withPath(path);
        return new ImmutableUrl(newMutableUrl);
    }

    /**
     * Adds a query-string parameter to the end of the url, like ?key=val.
     *
     * @param key The key for the query string.
     * @param value The value for the query string.
     * @return The Url with the query string param added (for chaining calls)
     */
    public ImmutableUrl withParam(String key, String value) {
        Url newMutableUrl = new Url(mutableUrl).withParam(key, value);
        return new ImmutableUrl(newMutableUrl);
    }

    /**
     * You can pass this object to all the get/post/put/delete etc methods.
     *
     * @return The textual representation of the Url, correctly formatted.
     */
    public String toString(){
        return mutableUrl.toString();
    }
}
