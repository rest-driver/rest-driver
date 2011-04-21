package com.googlecode.rd.serverdriver.http;

import com.googlecode.rd.serverdriver.http.request.*;
import com.googlecode.rd.serverdriver.http.response.DefaultResponse;
import com.googlecode.rd.serverdriver.http.response.Response;
import com.googlecode.rd.serverdriver.matchers.HasHeader;
import com.googlecode.rd.serverdriver.matchers.HasHeaderWithValue;
import com.googlecode.rd.serverdriver.matchers.HasResponseBody;
import com.googlecode.rd.serverdriver.matchers.HasStatusCode;
import com.googlecode.rd.serverdriver.xml.XmlAcceptanceTestHelper;
import com.googlecode.rd.types.Header;
import org.apache.commons.io.IOUtils;
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
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;

public final class RestServerDriver {

    private static final String ENCODING = "UTF-8";

    @Deprecated
    public static Header[] headers(final Header... headers) {
        // we'll do this with varargs now
        return headers;
    }



    public static Header header(final String name, final String value) {
        return new Header(name, value);
    }


    /*
     *   HTTP response matchers
     */

    public static TypeSafeMatcher<Response> hasStatusCode(final int statusCode) {
        return new HasStatusCode(is(statusCode));
    }

    public static TypeSafeMatcher<Response> hasStatusCode(final Matcher<Integer> statusCodeMatcher) {
        return new HasStatusCode(statusCodeMatcher);
    }

    public static TypeSafeMatcher<Response> hasResponseBody(final Matcher<String> bodyMatcher) {
        return new HasResponseBody(bodyMatcher);
    }

    public static TypeSafeMatcher<Response> hasHeader(final String name) {
        return new HasHeader(name);
    }

    public static TypeSafeMatcher<Response> hasHeaderWithValue(final String name, final Matcher<String> valueMatcher) {
        return new HasHeaderWithValue(name, valueMatcher);
    }



    /*
     *   HTTP GET methods
     */

    public static Response get(String url, Header... headers) {
        HttpGet request = new HttpGet(url);
        request.setHeaders( headersFromHeaderList( headers ) );
        return responseFromRequest(request);
    }


    public static Response getOf(String url, Header... headers)   { return get(url, headers); }
    public static Response doGetOf(String url, Header... headers) { return get(url, headers); }
    public static Response getting(String url, Header... headers) { return get(url, headers); }



    /*
     *   HTTP POST methods
     */

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

        return responseFromRequest(request);

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

        return responseFromRequest(request);

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

        return responseFromRequest(request);

    }

    public static Element asXml(Response response) {
        return XmlAcceptanceTestHelper.asXml(response.getContent());
    }

    /**
     * Reads in a resource from the class path
     *
     * @param fileName The file name to load
     * @return The content of the file
     */
    public static String fromFile(String fileName) {

        InputStream stream = RestServerDriver.class.getClassLoader().getResourceAsStream(fileName);

        if (stream == null) {
            throw new RuntimeException("Couldn't find file " + fileName);
        }

        try {
            return IOUtils.toString(stream, ENCODING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from file " + fileName, e);
        }

    }

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
            return new StringEntity(request.getContent(), ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error setting entity of request", e);
        }
    }

    private static Response responseFromRequest(HttpUriRequest request) {

        HttpClient httpClient = new DefaultHttpClient();

        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 0);

        HttpResponse response;

        try {

            long startTime = System.currentTimeMillis();
            response = httpClient.execute(request);
            long endTime = System.currentTimeMillis();

            return new DefaultResponse(statusCodeFromResponse(response), contentFromResponse(response), headersFromResponse(response), (endTime - startTime));

        } catch (ClientProtocolException e) {
            throw new RuntimeException("Error executing request", e);
        } catch (IOException e) {
            throw new RuntimeException("Error executing request", e);
        }

    }

    private static int statusCodeFromResponse(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    private static String contentFromResponse(HttpResponse response) {
        InputStream stream = null;
        String content;

        try {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                content = null;
            } else {
                stream = response.getEntity().getContent();
                content = IOUtils.toString(stream, ENCODING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting response entity", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return content;
    }

    private static List<Header> headersFromResponse(HttpResponse response) {
        List<Header> headers = new ArrayList<Header>();

        for (org.apache.http.Header currentHeader : response.getAllHeaders()) {
            Header header = new Header(currentHeader.getName(), currentHeader.getValue());
            headers.add(header);
        }

        return headers;
    }

}
