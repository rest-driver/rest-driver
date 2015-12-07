package com.github.restdriver.clientdriver;

import java.security.KeyStore;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.restdriver.clientdriver.jetty.DefaultClientDriverJettyHandler;

/**
 * Factory to create a {@link ClientDriver} object which supports SSL. To
 * arrange that a key store has to be provided. The required password has to be
 * the password of the certificate's private key as well as of the key store.
 * This is usually the case if the certificate is exported via OpenSSL e.g. as
 * PKCS#12 format.
 * 
 * An example to create a certificate with keytool provided in the JDK:
 * 
 * <pre>
 * {@code $ keytool -genkey -keyalg RSA -dname "cn=SecureClientDriver" \ 
 *  -alias certificate -keystore keystore.jks -keypass password \ 
 *  -storepass password -validity 360 -keysize 2048}
 * </pre>
 */
public class SecureClientDriverFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDriverFactory.class);

    private int port = 0;
    private String password;
    private String certAlias;
    private KeyStore keyStore;

    /**
     * Factory method to create and start ClientDriver. The port will be chosen
     * automatically.
     * 
     * @param keyStore
     *            The {@link KeyStore} that contains the necessary certificate.
     * @param password
     *            The password for the certificate private key & the container.
     * @param certAlias
     *            the alias of the certificate in the key store
     * @return a ClientDriver object
     */
    public SecureClientDriver createClientDriver(KeyStore keyStore, String password, String certAlias) {
        this.password = password;
        this.certAlias = certAlias;
        this.keyStore = keyStore;
        return this.build();
    }

    /**
     * Factory method to create and start a ClientDriver on a specific port. The
     * port can be set, the following arguments are to setup SSL.
     * 
     * @param port
     *            The port that should be used.
     * @param keyStore
     *            The {@link KeyStore} that contains the necessary certificate.
     * @param password
     *            The password for the certificate private key & the container.
     * @param certAlias
     *            the alias of the certificate in the key store
     * @return a ClientDriver object
     */
    public SecureClientDriver createClientDriver(int port, KeyStore keyStore, String password, String certAlias) {
        this.port = port;
        this.password = password;
        this.certAlias = certAlias;
        this.keyStore = keyStore;
        return this.build();
    }

    /**
     * Sets the port. By default the port is set to 0, which results in a freely
     * chosen port.
     * 
     * @param port
     *            the port
     * @return the factory object
     */
    public SecureClientDriverFactory port(int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the password. Password may not be null or empty when building. 
     * 
     * @param port
     *            the port
     * @return the factory object
     */
    public SecureClientDriverFactory password(String password) {
        this.password = password;
        return this;
    }

    public SecureClientDriverFactory certAlias(String certAlias) {
        this.certAlias = certAlias;
        return this;
    }

    public SecureClientDriverFactory keyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
        return this;
    }

    /**
     * Create SecureClientDriver with the given configuration.
     * 
     * @return
     */
    public SecureClientDriver build() {
        Validate.notEmpty(certAlias, "Certificate alias is not set.");
        Validate.notEmpty(password, "Password not set.");
        Validate.notNull(keyStore, "Key store is not set.");
        SecureClientDriver clientDriver = new SecureClientDriver(
                new DefaultClientDriverJettyHandler(new DefaultRequestMatcher()), port, keyStore, password, certAlias);
        LOGGER.debug("ClientDriver created at '" + clientDriver.getBaseUrl() + "'.");
        return clientDriver;
    }

}
