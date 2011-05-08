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

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.text.StrBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to help with building of URLs
 */
public class Url {

    private StrBuilder url;
    private List<QueryParam> queryParams;

    private class QueryParam {
        final String key;
        final String value;

        private QueryParam(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Setup a Url with a base path, like "http://localhost:8080".
     *
     * @param base the base Url
     */
    public Url(String base) {
        this.url = new StrBuilder(base);
        queryParams = new ArrayList<QueryParam>();
    }

    /**
     * Add a path to a url.  This method will ensure that there is always exactly one "/" character between segments (so you don't have to :).
     *
     * @param path the path, eg "foo/bar"
     * @return The new Url object (for chaining calls)
     */
    public Url withPath(String path) {

        if (!(url.endsWith("/") || path.startsWith("/"))) {
            url.append("/");
        }

        if (url.endsWith("/") && path.startsWith("/")) {
            path = path.substring(1);
        }

        url.append(path);
        return this;
    }

    /**
     * Adds a query-string parameter to the end of the url, like ?key=val.
     *
     * @param key   The key for the query string.
     * @param value The value for the query string.
     * @return The Url with the query string param added (for chaining calls)
     */
    public Url withParam(String key, String value) {
        queryParams.add(new QueryParam(key, value));
        return this;
    }

    /**
     * You can pass this object to all the get/post/put/delete etc methods
     *
     * @return The Url, correctly formatted.
     */
    public String toString() {

        boolean firstParam = true;

        StrBuilder escapedUri;

        try {
            escapedUri = new StrBuilder(URIUtil.encodePath(url.toString()));


            for (QueryParam qp : queryParams) {
                if (firstParam) {
                    escapedUri.append("?");
                    firstParam = false;
                } else {
                    escapedUri.append("&");
                }
                escapedUri
                        .append(URIUtil.encodeQuery(qp.getKey()))
                        .append("=")
                        .append(URIUtil.encodeQuery(qp.getValue()));
            }

        } catch (URIException urie) {
            throw new RuntimeException("bad uri ", urie);
        }

        return escapedUri.toString();
    }
}
