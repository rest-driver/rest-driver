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
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.junit.Test;

import com.github.restdriver.clientdriver.DefaultRequestMatcher;
import com.github.restdriver.clientdriver.SecureClientDriver;
import com.github.restdriver.clientdriver.jetty.DefaultClientDriverJettyHandler;

public class SecureClientDriverTest {

    @Test
    public void testConnectionSucceedsWithGivenTrustMaterial() throws Exception {

        // Arrange
        KeyStore keyStore = getKeystore();
        SecureClientDriver driver = new SecureClientDriver(
                new DefaultClientDriverJettyHandler(new DefaultRequestMatcher()), 1111, keyStore, "password",
                "certificate");
        driver.addExpectation(onRequestTo("/test"), giveEmptyResponse());

        // set the test certificate as trusted
        SSLContext context = SSLContexts.custom().loadTrustMaterial(keyStore, TrustSelfSignedStrategy.INSTANCE).build();
        HttpClient client = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setSSLContext(context).build();
        HttpGet getter = new HttpGet(driver.getBaseUrl() + "/test");

        // Act
        HttpResponse response = client.execute(getter);

        // Assert
        assertEquals(204, response.getStatusLine().getStatusCode());
        driver.verify();
    }

    @Test(expected = SSLHandshakeException.class)
    public void testConnectionFailsWithoutTrustMaterial() throws Exception {
        // Arrange
        KeyStore keyStore = getKeystore();
        SecureClientDriver driver = new SecureClientDriver(
                new DefaultClientDriverJettyHandler(new DefaultRequestMatcher()), keyStore, "password",
                "certificate");
        driver.addExpectation(onRequestTo("/test"), giveEmptyResponse());

        // set the test certificate as trusted
        HttpClient client = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        HttpGet getter = new HttpGet(driver.getBaseUrl() + "/test");

        // Act
        client.execute(getter);

    }
    
    @Test
    public void getBaseUrlStartsWithHttps() throws Exception {
        
        // Arrange
        KeyStore keyStore = getKeystore();
        SecureClientDriver driver = new SecureClientDriver(
                new DefaultClientDriverJettyHandler(new DefaultRequestMatcher()), keyStore, "password",
                "certificate");

        // Act
        String result = driver.getBaseUrl();

        // Assert
        assertThat(result, startsWith("https"));
    }
    
    
    static KeyStore getKeystore()
            throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        ClassLoader loader = SecureClientDriverTest.class.getClassLoader();
        byte[] binaryContent = IOUtils.toByteArray(loader.getResourceAsStream("keystore.jks"));
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new ByteArrayInputStream(binaryContent), "password".toCharArray());
        return keyStore;

    }

}
