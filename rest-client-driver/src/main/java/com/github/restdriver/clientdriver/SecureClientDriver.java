package com.github.restdriver.clientdriver;

import java.security.KeyStore;

import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.github.restdriver.clientdriver.jetty.ClientDriverJettyHandler;

/**
 * Secure client driver extends the {@link ClientDriver} to use secure
 * connections via HTTPS with a provided certificate.
 */
public class SecureClientDriver extends ClientDriver {

    KeyStore keyStore;
    String password;
    String certificateAlias;

    /**
     * Constructor which uses the given port to bind to. The server is started
     * during construction.
     * 
     * @param handler
     *            the {@link ClientDriverJettyHandler} to use.
     * @param port
     *            the port to bind to.
     * @param keyStore
     *            the key store to use for the certificate.
     * @param password
     *            the password for the certificate.
     * @param certificateAlias
     *            the alias of the certificate.
     */
    public SecureClientDriver(ClientDriverJettyHandler handler, int port, KeyStore keyStore, String password,
            String certificateAlias) {
        super();
        this.keyStore = keyStore;
        this.password = password;
        this.certificateAlias = certificateAlias;

        this.handler = handler;
        this.jettyServer = createAndStartJetty(port);
    }

    /**
     * Constructor which uses a free port to bind to. The server is started
     * during construction.
     * 
     * @param handler
     *            the {@link ClientDriverJettyHandler} to use.
     * @param keyStore
     *            the key store to use for the certificate.
     * @param password
     *            the password for the certificate.
     * @param certificateAlias
     *            the alias of the certificate.
     */
    public SecureClientDriver(ClientDriverJettyHandler handler, KeyStore keyStore, String password,
            String certificateAlias) {
        this(handler, 0, keyStore, password, certificateAlias);
    }

    @Override
    protected SslContextFactory getSslContextFactory() {
        SslContextFactory sslContextFactoryFactory = new SslContextFactory();
        sslContextFactoryFactory.setKeyStore(keyStore);
        sslContextFactoryFactory.setCertAlias(certificateAlias);
        sslContextFactoryFactory.setKeyStorePassword(password);
        sslContextFactoryFactory.setKeyManagerPassword(password);
        sslContextFactoryFactory.checkKeyStore();

        return sslContextFactoryFactory;
    }

    @Override
    public String getBaseUrl() {
        return "https://localhost:" + getPort();
    }

}
