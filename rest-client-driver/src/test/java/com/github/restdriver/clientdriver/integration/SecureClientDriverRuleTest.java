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

import static com.github.restdriver.clientdriver.RestClientDriver.giveEmptyResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.SecureClientDriverRule;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.clientdriver.exception.ClientDriverSetupException;

public class SecureClientDriverRuleTest {

	@Rule
	public SecureClientDriverRule rule = new SecureClientDriverRule(getKeystore(), "password", "certificate");
    @Rule
    public ExpectedException thrown = ExpectedException.none();

	@Test
	public void usageOfRuleWithMatchingCallSucceeds() throws Exception {

		// Arrange
		rule.addExpectation(onRequestTo("/test"), giveEmptyResponse());

		HttpClient client = getClient();
		HttpGet getter = new HttpGet(rule.getBaseUrl() + "/test");

		// Act
		HttpResponse response = client.execute(getter);

		// Assert
		assertEquals(204, response.getStatusLine().getStatusCode());
	}

	@Test
	public void usageOfRuleWithNotMatchingCallFails() throws Exception {

		// Arrange
		rule.addExpectation(onRequestTo("/test"), giveEmptyResponse());

		HttpClient client = getClient();
		HttpGet getter = new HttpGet(rule.getBaseUrl() + "/wrong");
		// Act
		client.execute(getter);
		// Assert
		thrown.expect(ClientDriverFailedExpectationException.class);
	}

	@Test
	public void usageOfRuleWithNotCallFails() throws Exception {

		// Arrange
		rule.addExpectation(onRequestTo("/test"), giveEmptyResponse());

		// Act

		// Assert
		thrown.expect(ClientDriverFailedExpectationException.class);
	}

	private static KeyStore getKeystore() {
		try {
			ClassLoader loader = SecureClientDriverTest.class.getClassLoader();
			byte[] binaryContent = IOUtils.toByteArray(loader.getResourceAsStream("keystore.jks"));
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new ByteArrayInputStream(binaryContent), "password".toCharArray());
			return keyStore;
		} catch (Exception e) {
			throw new ClientDriverSetupException("Key store could not be loaded.", e);
		}
	}

	private HttpClient getClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		try {
			// set the test certificate as trusted
			SSLContext context = SSLContexts.custom().loadTrustMaterial(getKeystore(), TrustSelfSignedStrategy.INSTANCE)
					.build();
			return HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).setSSLContext(context)
					.build();
		} catch (Exception e) {
			throw new ClientDriverSetupException("Client could not be created.", e);
		}
	}
}
