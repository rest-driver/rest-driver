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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to help with building of URLs.
 */
public final class Url {

    private StrBuilder url;
    private List<QueryParam> queryParams;

    /**
     * Encapsulates key & value for a query parameter.
     */
    private final class QueryParam {
        private final String key;
        private final String value;

        private QueryParam(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Setup a Url with a base path, like "http://localhost:8080".  You can also supply just "localhost" and the
     * "http" will be
     *
     * @param base the base Url.
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
     * You can pass this object to all the get/post/put/delete etc methods.
     *
     * @return The Url, correctly formatted.
     */
    public String toString() {

        String[] baseParts;

        if (url.toString().contains("://")) {
            baseParts = url.toString().split("://");

        } else {
            baseParts = new String[]{"http", url.toString()};
        }

        String scheme, ssp, path, query;

        scheme = baseParts[0];

        if (baseParts[1].contains("/")) {
            ssp = baseParts[1].substring(0, baseParts[1].indexOf("/"));
            path = baseParts[1].substring(baseParts[1].indexOf("/"));

        } else {
            ssp = baseParts[1];
            path = "";

        }

        query = StringUtils.trimToNull(StringUtils.join(queryParams, "&"));

        try {
            return new URI(scheme, ssp, path, query, null).toASCIIString();

        } catch (URISyntaxException use) {
            // NB not sure how this could get caused...
            throw new RuntimeUriSyntaxException("Cannot create URL", use);
        }

    }
}
