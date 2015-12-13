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

        return sslContextFactoryFactory;
    }

    @Override
    public String getBaseUrl() {
        return "https://localhost:" + getPort();
    }

}
