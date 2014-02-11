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
package com.github.restdriver.clientdriver.integration;

import static com.github.restdriver.Matchers.*;
import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Rule;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.clientdriver.capture.JsonBodyCapture;
import com.github.restdriver.clientdriver.capture.StringBodyCapture;

public class BodyCaptureTest {
    
    @Rule
    public ClientDriverRule clientDriver = new ClientDriverRule();
    
    @Test
    public void canCaptureRequestBodyAsString() throws Exception {
        
        StringBodyCapture capture = new StringBodyCapture();
        
        clientDriver.addExpectation(
                onRequestTo("/foo").withMethod(Method.POST).capturingBodyIn(capture),
                giveEmptyResponse().withStatus(201));
        
        HttpClient client = new DefaultHttpClient();
        HttpPost correctPost = new HttpPost(clientDriver.getBaseUrl() + "/foo");
        correctPost.setEntity(new StringEntity("a string"));
        HttpResponse correctResponse = client.execute(correctPost);
        EntityUtils.consume(correctResponse.getEntity());
        
        assertThat(capture.getContent(), is("a string"));
    }
    
    @Test
    public void canCaptureRequestBodyAsJson() throws Exception {
        
        JsonBodyCapture capture = new JsonBodyCapture();
        
        clientDriver.addExpectation(
                onRequestTo("/foo").withMethod(Method.POST).capturingBodyIn(capture),
                giveEmptyResponse().withStatus(201));
        
        HttpClient client = new DefaultHttpClient();
        HttpPost correctPost = new HttpPost(clientDriver.getBaseUrl() + "/foo");
        correctPost.setEntity(new StringEntity("{\"a\": \"A\"}"));
        HttpResponse correctResponse = client.execute(correctPost);
        EntityUtils.consume(correctResponse.getEntity());
        
        assertThat(capture.getContent(), hasJsonPath("$.a", equalTo("A")));
    }
    
}
