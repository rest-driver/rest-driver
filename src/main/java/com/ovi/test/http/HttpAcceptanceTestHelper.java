package com.ovi.test.http;

import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.util.StopWatch;
import org.w3c.dom.Element;

import com.ovi.test.matchers.HasHeader;
import com.ovi.test.matchers.HasHeaderWithValue;
import com.ovi.test.matchers.HasResponseBody;
import com.ovi.test.matchers.HasStatusCode;
import com.ovi.test.xml.XmlAcceptanceTestHelper;

public final class HttpAcceptanceTestHelper {

	private static final String ENCODING = "UTF-8";

	public static Header[] headers(final Header... headers) {
		return headers;
	}

	public static Header header(final String name, final String value) {
		return new Header(name, value);
	}

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

	public static Response get(final GetRequest getRequest) {
		return getting(getRequest);
	}

	public static Response getting(final GetRequest getRequest) {

		final HttpGet request = new HttpGet(getRequest.getUrl());
		request.setHeaders(headersFromRequest(getRequest));

		return responseFromRequest(request);

	}

	public static Response post(final PostRequest postRequest) {
		return posting(postRequest);
	}

	public static Response postOf(final PostRequest postRequest) {
		return posting(postRequest);
	}

	public static Response posting(final PostRequest postRequest) {

		final HttpPost request = new HttpPost(postRequest.getUrl());
		request.setHeaders(headersFromRequest(postRequest));
		request.setEntity(entityFromRequest(postRequest));

		return responseFromRequest(request);

	}

	public static Response put(final PutRequest putRequest) {
		return putting(putRequest);
	}

	public static Response putOf(final PutRequest putRequest) {
		return putting(putRequest);
	}

	public static Response putting(final PutRequest putRequest) {

		final HttpPut request = new HttpPut(putRequest.getUrl());
		request.setHeaders(headersFromRequest(putRequest));
		request.setEntity(entityFromRequest(putRequest));

		return responseFromRequest(request);

	}

	public static Response delete(final DeleteRequest deleteRequest) {
		return deleting(deleteRequest);
	}

	public static Response deleteOf(final DeleteRequest deleteRequest) {
		return deleting(deleteRequest);
	}

	public static Response deleting(final DeleteRequest deleteRequest) {

		final HttpDelete request = new HttpDelete(deleteRequest.getUrl());

		return responseFromRequest(request);

	}

	public static Element asXml(final Response response) {
		return XmlAcceptanceTestHelper.asXml(response.getContent());
	}

	/**
	 * Reads in a resource from the class path
	 * 
	 * @param fileName
	 *            The file name to load
	 * @return The content of the file
	 */
	public static String fromFile(final String fileName) {

		final InputStream stream = HttpAcceptanceTestHelper.class.getClassLoader().getResourceAsStream(fileName);

		if (stream == null) {
			throw new RuntimeException("Couldn't find file " + fileName);
		}

		try {
			return IOUtils.toString(stream, ENCODING);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to read from file " + fileName, e);
		}

	}

	private static org.apache.http.Header[] headersFromRequest(final Request request) {
		final List<org.apache.http.Header> headers = new ArrayList<org.apache.http.Header>();

		for (final Header header : request.getHeaders()) {
			headers.add(new BasicHeader(header.getName(), header.getValue()));
		}

		return headers.toArray(new org.apache.http.Header[headers.size()]);
	}

	private static HttpEntity entityFromRequest(final ContentRequest request) {
		try {
			return new StringEntity(request.getContent(), ENCODING);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Error setting entity of request", e);
		}
	}

	private static Response responseFromRequest(final HttpUriRequest request) {

		final HttpClient httpClient = new DefaultHttpClient();

		final HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 0);

		final StopWatch stopWatch = new StopWatch();

		final HttpResponse response;

		try {

			stopWatch.start();
			response = httpClient.execute(request);
			stopWatch.stop();

			return new DefaultResponse(statusCodeFromResponse(response), contentFromResponse(response), headersFromResponse(response), stopWatch.getLastTaskTimeMillis());

		} catch (final ClientProtocolException e) {
			throw new RuntimeException("Error executing request", e);
		} catch (final IOException e) {
			throw new RuntimeException("Error executing request", e);
		}

	}

	private static int statusCodeFromResponse(final HttpResponse response) {
		return response.getStatusLine().getStatusCode();
	}

	private static String contentFromResponse(final HttpResponse response) {
		InputStream stream = null;
		final String content;

		try {
			final HttpEntity entity = response.getEntity();
			if (entity == null) {
				content = null;
			} else {
				stream = response.getEntity().getContent();
				content = IOUtils.toString(stream, ENCODING);
			}
		} catch (final IOException e) {
			throw new RuntimeException("Error getting response entity", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}

		return content;
	}

	private static Header[] headersFromResponse(final HttpResponse response) {
		final List<Header> headers = new ArrayList<Header>();

		for (final org.apache.http.Header currentHeader : response.getAllHeaders()) {
			final Header header = new Header(currentHeader.getName(), currentHeader.getValue());
			headers.add(header);
		}

		return headers.toArray(new Header[headers.size()]);
	}

}
