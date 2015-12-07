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
		clientDriver = new SecureClientDriverFactory().createClientDriver(keyStore, password, certAlias);
	}

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
	public SecureClientDriverRule(int port, KeyStore keyStore, String password, String certAlias) {
		clientDriver = new SecureClientDriverFactory().createClientDriver(port, keyStore, password, certAlias);
	}
}
