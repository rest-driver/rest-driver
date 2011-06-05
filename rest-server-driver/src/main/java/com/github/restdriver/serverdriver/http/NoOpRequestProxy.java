package com.github.restdriver.serverdriver.http;

/**
 * This class instructs the server-driver to use no proxy.  This is the default
 * behaviour anyway, but can be specified explicitly.
 */
public final class NoOpRequestProxy implements AnyRequestModifier {

    @Override
    public void applyTo(ServerDriverHttpUriRequest request) {
        request.setProxyHost(null);
    }

    
    
}
