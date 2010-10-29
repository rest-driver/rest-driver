package com.ovi.test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ovi.test.matchers.ContainingValue;
import com.ovi.test.matchers.HasJsonArray;
import com.ovi.test.matchers.HasJsonValue;
import com.ovi.test.matchers.HasResponseBody;
import com.ovi.test.matchers.HasStatusCode;
import com.ovi.test.matchers.IsValid;
import com.ovi.test.matchers.WithSize;
import com.ovi.test.matchers.WithValueAt;

public class AcceptanceTestHelper {

    private static final HttpClient client = new HttpClient();

    public static HttpMethod doGetOf(final Object urlObject) {
        String url = urlObject.toString();
        final HttpMethod method = new GetMethod(url);
        return callHttpMethod(method);
    }

    public static HttpMethod doPutOf(String url, InputStream requestBody, String contentType) {

        final PutMethod method = new PutMethod(url);
        method.setRequestEntity(new InputStreamRequestEntity(requestBody, contentType));

        return callHttpMethod(method);

    }

    public static HttpMethod doPutOf(String url) {

        final PutMethod method = new PutMethod(url);
        return callHttpMethod(method);

    }

    public static HttpMethod doPostOf(String url, InputStream requestBody, String contentType) {

        final PostMethod method = new PostMethod(url);
        method.setRequestEntity(new InputStreamRequestEntity(requestBody, contentType));

        return callHttpMethod(method);

    }

    private static HttpMethod callHttpMethod(HttpMethod method) {

        try {
            client.executeMethod(method);
        } catch (final HttpException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return method;
    }

    public static InputStream getClasspathResourceAsStream(String xmlClasspathResource) {

        final InputStream xmlContent = AcceptanceTestHelper.class.getResourceAsStream(xmlClasspathResource);

        if (xmlContent == null) {
            throw new RuntimeException(new FileNotFoundException(xmlClasspathResource));
        }

        return xmlContent;

    }

    public static String getClasspathResourceAsString(String xmlClasspathResource) throws IOException {
        InputStream is = null;

        try {
            is = getClasspathResourceAsStream(xmlClasspathResource);
            return IOUtils.toString(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static Node xmlToNode(final String xmlString) {

        try {

            final Element node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xmlString.getBytes())).getDocumentElement();

            return node;

        } catch (final SAXException e) {
            throw new RuntimeException("Failed to parse XML.  Source string was: " + xmlString);

        } catch (final IOException e) {
            throw new RuntimeException("Failed to parse XML.  Source string was: " + xmlString);

        } catch (final ParserConfigurationException e) {
            throw new RuntimeException("Failed to parse XML.  Source string was: " + xmlString);

        }

    }

    public static Node extractXmlNodeFrom(HttpMethod method) {

        try {
            String responseBody = IOUtils.toString(method.getResponseBodyAsStream());
            return xmlToNode(responseBody);

        } catch (IOException e) {
            throw new RuntimeException("Could not extract XML object from response body", e);

        }
    }

    public static JsonNode jsonToNode(final String jsonString) {

        final ObjectMapper mapper = new ObjectMapper();

        try {

            return mapper.readValue(jsonString, JsonNode.class);

        } catch (final JsonParseException e) {
            throw new RuntimeException("Failed to parse Json.  Source string was: " + jsonString, e);

        } catch (final JsonMappingException e) {
            throw new RuntimeException("Failed to parse Json.  Source string was: " + jsonString, e);

        } catch (final IOException e) {
            throw new RuntimeException("Failed to parse Json.  Source string was: " + jsonString, e);

        }

    }

    public static JsonNode extractJsonNodeFrom(HttpMethod method) {

        try {
            String responseBody = IOUtils.toString(method.getResponseBodyAsStream());
            return jsonToNode(responseBody);

        } catch (IOException e) {
            throw new RuntimeException("Could not extract JSON object from response body", e);

        }
    }

    // Hamcrest matchers

    public static TypeSafeMatcher<HttpMethod> hasStatusCode(int expectedStatusCode) {
        return new HasStatusCode(expectedStatusCode);
    }

    public static TypeSafeMatcher<HttpMethod> hasResponseBody(Matcher<String> bodyMatcher) {
        return new HasResponseBody(bodyMatcher);
    }

    public static TypeSafeMatcher<JsonNode> hasJsonValue(String fieldName, Matcher<?> matcher) {
        return new HasJsonValue(fieldName, matcher);
    }

    public static TypeSafeMatcher<JsonNode> hasJsonArray(String fieldName, Matcher<?> matcher) {
        return new HasJsonArray(fieldName, matcher);
    }

    public static TypeSafeMatcher<JsonNode> containingValue(Matcher<?> matcher) {
        return new ContainingValue(matcher);
    }

    public static TypeSafeMatcher<JsonNode> withValueAt(int position, Matcher<?> matcher) {
        return new WithValueAt(position, matcher);
    }

    public static TypeSafeMatcher<JsonNode> withSize(Matcher<?> matcher) {
        return new WithSize(matcher);
    }

    public static TypeSafeMatcher<JsonNode> valid(JsonNode schema) {
        return new IsValid(schema);
    }

    public static TypeSafeMatcher<JsonNode> valid(URL url) {
        return new IsValid(url);
    }

}
