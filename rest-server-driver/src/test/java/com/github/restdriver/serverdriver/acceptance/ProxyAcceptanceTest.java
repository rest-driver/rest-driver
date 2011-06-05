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

import static com.github.restdriver.serverdriver.RestServerDriver.get;
import static com.github.restdriver.serverdriver.RestServerDriver.usingProxy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import javax.net.ssl.HandshakeCompletedEvent;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.ProxyServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.serverdriver.http.exception.RuntimeHttpHostConnectException;
import com.github.restdriver.serverdriver.http.response.Response;

public class ProxyAcceptanceTest {

    private int proxyPort;
    private Server proxyServer;

    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testWithSpecifiedProxyFailsIfProxyIsNotAvailable() {
        thrown.expect(RuntimeHttpHostConnectException.class);
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content"));
        get(driver.getBaseUrl() + "/foo", usingProxy("localhost", ClientDriver.getFreePort()));
    }

    @Test
    public void testWithSpecifiedProxyPassesIfProxyIsAvailable() {
        startLocalProxy();
        driver.addExpectation(new ClientDriverRequest("/foo"), new ClientDriverResponse("Content"));
        get(driver.getBaseUrl() + "/foo", usingProxy("localhost", proxyPort));
        stopLocalProxy();
    }

    private void startLocalProxy() {
        try {

            int port = ClientDriver.getFreePort();

            proxyServer = new Server(port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            proxyServer.setHandler(context);
            context.addServlet(new ServletHolder(new ProxyServlet()), "/*");
            proxyServer.start();

            proxyPort = port;

        } catch (Exception e) {
            throw new RuntimeException("Proxy setup oops", e);
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
