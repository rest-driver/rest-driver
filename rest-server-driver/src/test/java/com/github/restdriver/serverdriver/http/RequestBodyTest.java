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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

public class RequestBodyTest {

    @Test
    public void bodyAppliesItselfToRequest() throws Exception {
        HttpPost request = new HttpPost();
        RequestBody body = new RequestBody("content", "contentType");
        body.applyTo(new ServerDriverHttpUriRequest(request));
        assertThat(IOUtils.toString(request.getEntity().getContent()), is("content"));
        assertThat(request.getEntity().getContentType().getValue(), is("contentType; charset=UTF-8"));
        assertThat(request.getFirstHeader("Content-type").getValue(), is("contentType"));
    }

    @Test
    public void applyToHandlesRequestWhichCannotHaveBody() {
        HttpUriRequest request = new HttpGet();
        RequestBody body = new RequestBody("content", "contentType");
        body.applyTo(new ServerDriverHttpUriRequest(request));
    }

}
