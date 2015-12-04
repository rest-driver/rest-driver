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
package com.github.restdriver.serverdriver.acceptance;

import static com.github.restdriver.serverdriver.RestServerDriver.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.SocketUtil;
import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.serverdriver.http.exception.RuntimeHttpHostConnectException;

public class ProxyAcceptanceTest {
    
    /* These are set when you call startLocalProxy() */
    private int proxyPort;
    private Server proxyServer;
    private int proxyHits = 0; // increments every time the proxy is used
    
    @Rule
    public ClientDriverRule driver = new ClientDriverRule();
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testWithSpecifiedProxyFailsIfProxyIsNotAvailable() throws IOException {
        thrown.expect(RuntimeHttpHostConnectException.class);
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        get(driver.getBaseUrl() + "/foo", usingProxy("localhost", SocketUtil.getFreePort()));
    }
    
    @Test
    public void testWithSpecifiedProxyPassesIfProxyIsAvailable() {
        startLocalProxy();
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        get(driver.getBaseUrl() + "/foo", usingProxy("localhost", proxyPort));
        assertThat(proxyHits, is(1));
        stopLocalProxy();
    }
    
    @Test
    public void testWithNoProxyDoesntTryToUseAProxy() {
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        get(driver.getBaseUrl() + "/foo", notUsingProxy());
        assertThat(proxyHits, is(0));
    }
    
    @Test
    public void whenMultipleProxiesAreSpecifiedLastOneWinsNoProxy() throws IOException {
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        get(driver.getBaseUrl() + "/foo", usingProxy("localhost", SocketUtil.getFreePort()), notUsingProxy());
        assertThat(proxyHits, is(0));
    }
    
    @Test
    public void whenMultipleProxiesAreSpecifiedLastOneWinsWithProxy() {
        startLocalProxy();
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        get(driver.getBaseUrl() + "/foo", notUsingProxy(), usingProxy("localhost", proxyPort));
        stopLocalProxy();
        assertThat(proxyHits, is(1));
    }
    
    @Test
    public void twoCallsWithOnlyOneProxiedOnlyUsesProxyOnce() {
        
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        startLocalProxy();
        
        get(driver.getBaseUrl() + "/foo");
        assertThat(proxyHits, is(0));
        
        get(driver.getBaseUrl() + "/foo", usingProxy("localhost", proxyPort));
        assertThat(proxyHits, is(1));
        
        get(driver.getBaseUrl() + "/foo", notUsingProxy());
        assertThat(proxyHits, is(1));
        
        stopLocalProxy();
    }
    
    @Test
    public void systemProxyUsesSystemProperties() {
        
        startLocalProxy();
        
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "" + proxyPort);
        
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        
        get(driver.getBaseUrl() + "/foo", usingSystemProxy());
        assertThat(proxyHits, is(1));
        
    }
    
    @Test
    public void systemProxyUsesNoProxyIfNoSystemPropertiesSet() {
        
        startLocalProxy();
        
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "");
        
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content", "text/plain"));
        
        get(driver.getBaseUrl() + "/foo", usingSystemProxy());
        assertThat(proxyHits, is(0));
        
        stopLocalProxy();
    }
    
    // //////////////////
    // Proxy helper stuff
    // //////////////////
    
    private void startLocalProxy() {
        try {
            
            int port = SocketUtil.getFreePort();
            
            proxyServer = new Server(port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            proxyServer.setHandler(context);
            context.addServlet(new ServletHolder("CountingProxyServlet", new CountingProxyServlet()), "/*");
            proxyServer.start();
            
            proxyPort = port;
            
        } catch (Exception e) {
            throw new RuntimeException("Proxy setup oops", e);
        }
    }
    
    private class CountingProxyServlet extends ProxyServlet {
        @Override
        public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
            proxyHits++;
            super.service(req, res);
        }
    }
    
    public void stopLocalProxy() {
        try {
            proxyServer.stop();
        } catch (Exception e) {
            throw new RuntimeException("Problem stopping Jetty proxy", e);
        }
    }
    
}
