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
package com.github.restdriver.serverdriver;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import com.github.restdriver.serverdriver.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.github.restdriver.serverdriver.http.exception.RuntimeClientProtocolException;
import com.github.restdriver.serverdriver.http.exception.RuntimeHttpHostConnectException;
import com.github.restdriver.serverdriver.http.exception.RuntimeUnknownHostException;
import com.github.restdriver.serverdriver.http.response.DefaultResponse;
import com.github.restdriver.serverdriver.http.response.Response;

/**
 * Provides static methods for performing HTTP requests against a resource.
 * 
 * @author mjg
 */
public final class RestServerDriver {
    
    private static final int DEFAULT_HTTP_PROXY_PORT = 80;
    public static final long DEFAULT_CONNECTION_TIMEOUT = 10000;
    public static final long DEFAULT_SOCKET_TIMEOUT = 10000;
    
    private RestServerDriver() {
    }
    
    /* ****************************************************************************
     * Helper methods to make value objects *
     * ****************************************************************************
     */

    /**
     * Make a Header.
     * 
     * @param name The name for the header
     * @param value The value for the header
     * @return The new header instance
     */
    public static Header header(String name, String value) {
        return new Header(name, value);
    }
    
    /**
     * Make a Header.
     * 
     * @param nameAndValue The name and value of the header in the form "name: value"
     * @return The new header instance
     */
    public static Header header(String nameAndValue) {
        return new Header(nameAndValue);
    }
    
    /**
     * Make a RequestBody for PUT or POST.
     * 
     * @param content Request body content as String.
     * @param contentType content-type eg text/plain.
     * @return The new request body instance.
     */
    public static RequestBody body(String content, String contentType) {
        return new RequestBody(content, contentType);
    }
    
    /**
     * Make a RequestBody from a byte array.
     * 
     * @param content Request body content as a byte array.
     * @param contentType Content-Type eg application/pdf.
     * @return The new request body instance.
     */
    public static ByteArrayRequestBody body(byte[] content, String contentType) {
        return new ByteArrayRequestBody(content, contentType);
    }
    
    /**
     * Use a user-specified proxy.
     * 
     * @param proxyHost The host.
     * @param proxyPort The port.
     * @return The new RequestProxy instance.
     */
    public static RequestProxy usingProxy(String proxyHost, int proxyPort) {
        return new RequestProxy(proxyHost, proxyPort);
    }
    
    /**
     * Do not use a proxy. This is the default anyway, but allowed for clarity.
     * 
     * @return The new NoOpRequestProxy instance.
     */
    public static NoOpRequestProxy notUsingProxy() {
        return new NoOpRequestProxy();
    }
    
    /**
     * Use the system proxy. These can be set with -Dhttp.proxyHost and -Dhttp.proxyPort.
     * This does not respect environment variables like HTTP_PROXY & friends.
     * 
     * @return The RequestProxy instance.
     */
    public static AnyRequestModifier usingSystemProxy() {
        
        String proxyHost = System.getProperty("http.proxyHost");
        int proxyPort = getSystemProxyPort();
        
        if (proxyHost.isEmpty()) {
            return new NoOpRequestProxy();
        }
        
        return new RequestProxy(proxyHost, proxyPort);
    }
    
    /**
     * Defaults to 80 as per UrlConnection.
     * 
     * @return The proxy port
     */
    private static int getSystemProxyPort() {
        try {
            return Integer.parseInt(System.getProperty("http.proxyPort"));
        } catch (NumberFormatException nfe) {
            return DEFAULT_HTTP_PROXY_PORT;
        }
    }
    
    /**
     * Use a single timeout value for both connection and socket timeouts.
     * 
     * @param timeout The timeout duration to use.
     * @param timeUnit The unit of the timeout.
     * 
     * @return The RequestTimeout instance.
     */
    public static AnyRequestModifier withTimeout(int timeout, TimeUnit timeUnit) {
        return new RequestTimeout(timeUnit.toMillis(timeout), timeUnit.toMillis(timeout));
    }
    
    /**
     * Specify a connection timeout.
     * 
     * @param timeout The timeout duration to use.
     * @param timeUnit The unit of the timeout.
     * 
     * @return The RequestConnectionTimeout instance.
     */
    public static AnyRequestModifier withConnectionTimeout(int timeout, TimeUnit timeUnit) {
        return new RequestConnectionTimeout(timeUnit.toMillis(timeout));
    }
    
    /**
     * Specify a socket timeout.
     * 
     * @param timeout The timeout duration to use.
     * @param timeUnit The unit of the timeout.
     * 
     * @return The RequestSocketTimeout instance.
     */
    public static AnyRequestModifier withSocketTimeout(int timeout, TimeUnit timeUnit) {
        return new RequestSocketTimeout(timeUnit.toMillis(timeout));
    }
    
    /**
     * Creates a new {@link Url} object.
     * 
     * @param base The base, like "http://localhost"
     * @return The Url object
     */
    public static Url url(String base) {
        return new Url(base);
    }
    
    /* ****************************************************************************
     * HTTP OPTIONS methods *
     * ****************************************************************************
     */

    /**
     * Perform an HTTP OPTIONS on a resource.
     * 
     * @param url The URL of a resource. Accepts any Object and calls .toString() on it.
     * @return A Response encapsulating the server's reply.
     */
    public static Response options(Object url) {
        ServerDriverHttpUriRequest request = new ServerDriverHttpUriRequest(new HttpOptions(url.toString()));
        return doHttpRequest(request);
    }
    
    /**
     * Synonym for {@link #options(Object)}.
     * 
     * @param url The URL of a resource. Accepts any Object and calls .toString() on it.
     * @return A Response encapsulating the server's reply.
     */
    public static Response optionsOf(Object url) {
        return options(url);
    }
    
    /* ****************************************************************************
     * HTTP GET methods *
     * ****************************************************************************
     */

    /**
     * Perform an HTTP GET on a resource.
     * 
     * @param url The URL of a resource. Accepts any Object and calls .toString() on it.
     * @param modifiers Optional HTTP headers to put on the request.
     * @return A Response encapsulating the server's reply.
     */
    public static Response get(Object url, AnyRequestModifier... modifiers) {
        ServerDriverHttpUriRequest request = new ServerDriverHttpUriRequest(new HttpGet(url.toString()));
        applyModifiersToRequest(modifiers, request);
        return doHttpRequest(request);
    }
    
    /**
     * Synonym for {@link #get(Object, AnyRequestModifier...)}.
     * 
     * @param url The URL of a resource. Accepts any Object and calls .toString() on it.
     * @param modifiers Optional HTTP headers to put on the request.
     * @return A Response encapsulating the server's reply.
     */
    public static Response getOf(Object url, AnyRequestModifier... modifiers) {
        return get(url, modifiers);
    }
    
    /**
     * Synonym for {@link #get(Object, AnyRequestModifier...)}.
     * 
     * @param url The URL of a resource. Accepts any Object and calls .toString() on it.
     * @param modifiers Optional HTTP headers to put on the request.
     * @return A Response encapsulating the server's reply.
     */
    public static Response doGetOf(Object url, AnyRequestModifier... modifiers) {
        return get(url, modifiers);
    }
    
    /**
     * Synonym for {@link #get(Object, AnyRequestModifier...)}.
     * 
     * @param url The URL of a resource. Accepts any Object and calls .toString() on it.
     * @param modifiers Optional HTTP headers to put on the request.
     * @return A Response encapsulating the server's reply.
     */
    public static Response getting(Object url, AnyRequestModifier... modifiers) {
        return get(url, modifiers);
    }
    
    /* ****************************************************************************
     * HTTP POST methods *
     * ****************************************************************************
     */

    /**
     * Perform an HTTP POST to the given URL.
     * 
     * @param url The URL. Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     * @return Response encapsulating the server's reply
     */
    public static Response post(Object url, BodyableRequestModifier... modifiers) {
        ServerDriverHttpUriRequest request = new ServerDriverHttpUriRequest(new HttpPost(url.toString()));
        applyModifiersToRequest(modifiers, request);
        return doHttpRequest(request);
    }
    
    /**
     * Synonym for {@link #post(Object, BodyableRequestModifier...)}.
     * 
     * @param url The URL. Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     * @return Response encapsulating the server's reply
     */
    public static Response postOf(Object url, BodyableRequestModifier... modifiers) {
        return post(url, modifiers);
    }
    
    /**
     * Synonym for {@link #post(Object, BodyableRequestModifier...)}.
     * 
     * @param url The URL. Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     * @return Response encapsulating the server's reply
     */
    public static Response doPostOf(Object url, BodyableRequestModifier... modifiers) {
        return post(url, modifiers);
    }
    
    /**
     * Synonym for {@link #post(Object, BodyableRequestModifier...)}.
     * 
     * @param url The URL. Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     * @return Response encapsulating the server's reply
     */
    public static Response posting(Object url, BodyableRequestModifier... modifiers) {
        return post(url, modifiers);
    }
    
    /* ****************************************************************************
     * HTTP PUT methods *
     * ****************************************************************************
     */

    /**
     * Perform an HTTP PUT to the given URL.
     * 
     * @param url The URL. Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     * @return Response encapsulating the server's reply
     */
    public static Response put(Object url, BodyableRequestModifier... modifiers) {
        ServerDriverHttpUriRequest request = new ServerDriverHttpUriRequest(new HttpPut(url.toString()));
        applyModifiersToRequest(modifiers, request);
        return doHttpRequest(request);
    }
    
    /**
     * Synonym for {@link #put(Object, BodyableRequestModifier...)}.
     * 
     * @param url The URL. Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     * @return Response encapsulating the server's reply
     */
    public static Response putOf(Object url, BodyableRequestModifier... modifiers) {
        return put(url, modifiers);
    }
    
    /**
     * Synonym for {@link #put(Object, BodyableRequestModifier...)}.
     * 
     * @param url The URL. Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     * @return Response encapsulating the server's reply
     */
    public static Response doPutOf(Object url, BodyableRequestModifier... modifiers) {
        return put(url, modifiers);
    }
    
    /**
     * Synonym for {@link #put(Object, BodyableRequestModifier...)}.
     * 
     * @param url The URL. Any object may be passed, we will call .toString() on it.
     * @param modifiers The modifiers to be applied to the request.
     * @return Response encapsulating the server's reply
     */
    public static Response putting(Object url, BodyableRequestModifier... modifiers) {
        return put(url, modifiers);
    }
    
    /* ****************************************************************************
     * HTTP DELETE methods *
     * ****************************************************************************
     */

    /**
     * Send an HTTP delete.
     * 
     * @param url The resource to delete
     * @param modifiers Any http headers
     * @return Response encapsulating the server's reply
     */
    public static Response delete(Object url, AnyRequestModifier... modifiers) {
        ServerDriverHttpUriRequest request = new ServerDriverHttpUriRequest(new HttpDelete(url.toString()));
        applyModifiersToRequest(modifiers, request);
        return doHttpRequest(request);
    }
    
    /**
     * Synonym for {@link #delete(Object, AnyRequestModifier...)}.
     * 
     * @param url The resource to delete
     * @param modifiers Any http headers
     * @return Response encapsulating the server's reply
     */
    public static Response deleteOf(Object url, AnyRequestModifier... modifiers) {
        return delete(url, modifiers);
    }
    
    /**
     * Synonym for {@link #delete(Object, AnyRequestModifier...)}.
     * 
     * @param url The resource to delete
     * @param modifiers Any http headers
     * @return Response encapsulating the server's reply
     */
    public static Response doDeleteOf(Object url, AnyRequestModifier... modifiers) {
        return delete(url, modifiers);
    }
    
    /**
     * Synonym for {@link #delete(Object, AnyRequestModifier...)}.
     * 
     * @param url The resource to delete
     * @param modifiers Any http headers
     * @return Response encapsulating the server's reply
     */
    public static Response deleting(Object url, AnyRequestModifier... modifiers) {
        return delete(url, modifiers);
    }
    
    /* ****************************************************************************
     * HTTP HEAD methods *
     * ****************************************************************************
     */

    /**
     * Perform an HTTP HEAD on a resource.
     * 
     * @param url The URL of a resource. Accepts any Object and calls .toString() on it.
     * @param modifiers Optional HTTP headers to put on the request.
     * @return A Response encapsulating the server's reply.
     */
    public static Response head(Object url, AnyRequestModifier... modifiers) {
        ServerDriverHttpUriRequest request = new ServerDriverHttpUriRequest(new HttpHead(url.toString()));
        applyModifiersToRequest(modifiers, request);
        return doHttpRequest(request);
    }
    
    /**
     * Synonym for {@link #head(Object, AnyRequestModifier...)}.
     * 
     * @param url The URL of a resource. Accepts any Object and calls .toString() on it.
     * @param modifiers Optional HTTP headers to put on the request.
     * @return A Response encapsulating the server's reply.
     */
    public static Response headOf(Object url, AnyRequestModifier... modifiers) {
        return head(url, modifiers);
    }
    
    /**
     * Synonym for {@link #head(Object, AnyRequestModifier...)}.
     * 
     * @param url The URL of a resource. Accepts any Object and calls .toString() on it.
     * @param modifiers Optional HTTP headers to put on the request.
     * @return A Response encapsulating the server's reply.
     */
    public static Response doHeadOf(Object url, AnyRequestModifier... modifiers) {
        return head(url, modifiers);
    }
    
    /*
     * Internal methods for creating requests and responses
     */
    private static void applyModifiersToRequest(BodyableRequestModifier[] modifiers, ServerDriverHttpUriRequest request) {
        if (modifiers == null) {
            return;
        }
        
        for (BodyableRequestModifier modifier : modifiers) {
            modifier.applyTo(request);
        }
    }
    
    /*
     * This is the method which actually makes http requests over the wire.
     */
    private static Response doHttpRequest(ServerDriverHttpUriRequest request) {
        
        HttpClient httpClient = new DefaultHttpClient();
        
        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, (int) request.getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(httpParams, (int) request.getSocketTimeout());
        HttpClientParams.setRedirecting(httpParams, false);
        
        if (request.getProxyHost() != null) {
            httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, request.getProxyHost());
        }
        
        HttpResponse response;
        
        try {
            long startTime = System.currentTimeMillis();
            response = httpClient.execute(request.getHttpUriRequest());
            long endTime = System.currentTimeMillis();
            
            return new DefaultResponse(response, (endTime - startTime));
            
        } catch (ClientProtocolException cpe) {
            throw new RuntimeClientProtocolException(cpe);
            
        } catch (UnknownHostException uhe) {
            throw new RuntimeUnknownHostException(uhe);
            
        } catch (HttpHostConnectException hhce) {
            throw new RuntimeHttpHostConnectException(hhce);
            
        } catch (IOException e) {
            throw new RuntimeException("Error executing request", e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        
    }
}
