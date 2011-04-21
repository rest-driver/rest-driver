package com.github.restdriver.serverdriver.http;

import com.github.restdriver.serverdriver.http.request.*;
import com.github.restdriver.serverdriver.http.response.Response;
import com.github.restdriver.serverdriver.http.response.DefaultResponse;
import com.github.restdriver.types.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public final class RestServerDriver {

    private static final String DEFAULT_CONTENT_ENCODING = "UTF-8";

    /*
     * Helpful to make this available here
     */

    public static Header header(final String name, final String value) {
        return new Header(name, value);
    }



    /******************************************************************************
     *                               HTTP GET methods                             *
     ******************************************************************************/

    /**
     * Perform an HTTP GET on a resource.
     *
     * @param url The URL of a resource.  Accepts any Object and calls .toString() on it.
     * @param headers Optional HTTP headers to put on the request.
     *
     * @return A Response encapsulating the server's reply.
     */
    public static Response get(Object url, Header... headers) {
        HttpGet request = new HttpGet(url.toString());
        request.setHeaders( headersFromHeaderList( headers ) );
        return makeHttpRequest(request);
    }


    /**
     * Synonym for {@link #get(Object, Header...)}
     */
    public static Response getOf(Object url, Header... headers)   { return get(url, headers); }

    /**
     * Synonym for {@link #get(Object, Header...)}
     */
    public static Response doGetOf(Object url, Header... headers) { return get(url, headers); }

    /**
     * Synonym for {@link #get(Object, Header...)}
     */
    public static Response getting(Object url, Header... headers) { return get(url, headers); }



    /******************************************************************************
     *                              HTTP POST methods                             *
     ******************************************************************************/

//    public static Response post(Object url, String){
//
//    }

    public static Response post(PostRequest postRequest) {
        return posting(postRequest);
    }

    public static Response postOf(PostRequest postRequest) {
        return posting(postRequest);
    }

    public static Response posting(PostRequest postRequest) {

        HttpPost request = new HttpPost(postRequest.getUrl());
        request.setHeaders(headersFromRequest(postRequest));
        request.setEntity(entityFromRequest(postRequest));

        return makeHttpRequest(request);

    }


    /*
     *   HTTP PUT methods
     */

    public static Response put(PutRequest putRequest) {
        return putting(putRequest);
    }

    public static Response putOf(PutRequest putRequest) {
        return putting(putRequest);
    }

    public static Response putting(PutRequest putRequest) {

        HttpPut request = new HttpPut(putRequest.getUrl());
        request.setHeaders(headersFromRequest(putRequest));
        request.setEntity(entityFromRequest(putRequest));

        return makeHttpRequest(request);

    }


    /*
     *   HTTP DELETE methods
     */

    public static Response delete(DeleteRequest deleteRequest) {
        return deleting(deleteRequest);
    }

    public static Response deleteOf(DeleteRequest deleteRequest) {
        return deleting(deleteRequest);
    }

    public static Response deleting(DeleteRequest deleteRequest) {

        HttpDelete request = new HttpDelete(deleteRequest.getUrl());

        return makeHttpRequest(request);

    }



    /*
     * Internal methods for creating requests and responses
     */

    private static org.apache.http.Header[] headersFromRequest(Request request) {
        List<org.apache.http.Header> headers = new ArrayList<org.apache.http.Header>();

        if (request.getHeaders() != null) {
            for (Header header : request.getHeaders()) {
                headers.add(new BasicHeader(header.getName(), header.getValue()));
            }
        }

        return headers.toArray(new org.apache.http.Header[headers.size()]);
    }

    private static org.apache.http.Header[] headersFromHeaderList(Header[] headerList) {
        List<org.apache.http.Header> headers = new ArrayList<org.apache.http.Header>();

        if (headerList != null) {
            for (Header header : headerList) {
                headers.add(new BasicHeader(header.getName(), header.getValue()));
            }
        }

        return headers.toArray(new org.apache.http.Header[headers.size()]);
    }


    private static HttpEntity entityFromRequest(ContentRequest request) {
        try {
            return new StringEntity(request.getContent(), DEFAULT_CONTENT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error setting entity of request", e);
        }
    }


    /**
     * This is the method which actually makes http requests over the wire
     *
     * @param request The Apache Http request to make
     *
     * @return Our wrapped response type
     */
    private static Response makeHttpRequest(HttpUriRequest request) {

        HttpClient httpClient = new DefaultHttpClient();

        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 0);

        HttpResponse response;

        try {
            long startTime = System.currentTimeMillis();
            response = httpClient.execute(request);
            long endTime = System.currentTimeMillis();

            return new DefaultResponse( response, (endTime - startTime) );

        } catch (ClientProtocolException e) {
            throw new RuntimeException("Error executing request", e);
        } catch (IOException e) {
            throw new RuntimeException("Error executing request", e);
        }

    }

}
