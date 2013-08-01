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

import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.junit.Rule;
import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverRule;

public class BasicAuthTest {
    
    @Rule
    public ClientDriverRule clientDriver = new ClientDriverRule(12345);
    
    @Test
    public void basicAuthWorks() throws Exception {
        
        clientDriver.addExpectation(
                onRequestTo("/").withBasicAuth("Aladdin", "open sesame"),
                giveEmptyResponse().withStatus(418)).anyTimes();
        
        DefaultHttpClient client = new DefaultHttpClient();
        client.getCredentialsProvider().setCredentials(new AuthScope("localhost", AuthScope.ANY_PORT), new UsernamePasswordCredentials("Aladdin", "open sesame"));
        
        HttpHost host = new HttpHost("localhost", 12345);
        
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);
        
        BasicHttpContext context = new BasicHttpContext();
        context.setAttribute(ClientContext.AUTH_CACHE, authCache);
        
        List<String> authPrefs = new ArrayList<String>();
        authPrefs.add(AuthPolicy.BASIC);
        client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authPrefs);
        
        HttpGet get = new HttpGet(clientDriver.getBaseUrl() + "/");
        
        HttpResponse response = client.execute(host, get, context);
        
        assertThat(response.getStatusLine().getStatusCode(), is(418));
        
    }
    
}
