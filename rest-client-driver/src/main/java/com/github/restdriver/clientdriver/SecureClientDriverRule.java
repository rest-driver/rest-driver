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
package com.github.restdriver.clientdriver;

import java.security.KeyStore;

/**
 * The SecureClientDriverRule allows a user to specify expectations on the HTTPS
 * requests that are made against it.
 */
public class SecureClientDriverRule extends ClientDriverRule {

	/**
	 * Creates a new rule which binds the driver to a free port.
	 * 
	 * @param keyStore
	 *            the key store with the certificate
	 * @param password
	 *            the certificate's password
	 * @param certAlias
	 *            the alias of the certificate
	 */
	public SecureClientDriverRule(KeyStore keyStore, String password, String certAlias) {
		super(new SecureClientDriverFactory().createClientDriver(keyStore, password, certAlias));
	}

	/**
	 * Creates a new rule which binds the driver to a free port.
	 * 
	 * @param port
	 *            the port
	 * @param keyStore
	 *            the key store with the certificate
	 * @param password
	 *            the certificate's password
	 * @param certAlias
	 *            the alias of the certificate
	 */
	public SecureClientDriverRule(int port, KeyStore keyStore, String password, String certAlias) {
		super(new SecureClientDriverFactory().createClientDriver(port, keyStore, password, certAlias));
	}
}
