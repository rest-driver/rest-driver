package com.ovi.test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
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
	private static final Pattern HEADER_PATTERN = Pattern.compile("(.+)[ ]*:[ ]*(.+)"); 
    private static final HttpClient client = new HttpClient();

    /**
     * Executes a HTTP GET request to the URL specified. 
     * 
     * @param urlObject
     * 		The object representing the URL. The toString() method of the object will be used
     * 		to get a string representation of the URL.
     * @param headers
     * 		A list of headers to add to the HTTP request. Each header should be a string in the
     * 		same form as one would add to the HTTP request itself: e.g. "someheader: somevalue".
     * @return
     * 		A handle to the GET {@link HttpMethod} after having been executed.  
     */
    public static HttpMethod doGetOf(final Object urlObject, final String... headers) {
        final String url = urlObject.toString();
        
        final HttpMethod method = new GetMethod(url);
        addHeaders(method, headers);
        
        return callHttpMethod(method);
    }

    /**
     * Executes a HTTP PUT request to the URL specified. 
     * 
     * @param urlObject
     * 		The object representing the URL. The toString() method of the object will be used
     * 		to get a string representation of the URL.
     * @param requestBody
     * 		The request entity body.
     * @param contentType
     * 		The request entity content type. NOTE: this value will be set as the "Content-Type"
     * 		header for the request ONLY if the "Content-Type" header is not set specifically in the headers
     * 		parameter. 
     * @param headers
     * 		A list of headers to add to the HTTP request. Each header should be a string in the
     * 		same form as one would add to the HTTP request itself: e.g. "someheader: somevalue".
     * @return
     * 		A handle to the PUT {@link HttpMethod} after having been executed.  
     */
    public static HttpMethod doPutOf(final String urlObject, final InputStream requestBody, final String contentType, final String... headers) {
        final String url = urlObject.toString();
        
        final PutMethod method = new PutMethod(url);
        addHeaders(method, headers);
        
        method.setRequestEntity(new InputStreamRequestEntity(requestBody, contentType));

        return callHttpMethod(method);

    }

    /**
     * Executes a HTTP PUT request to the URL specified. 
     * 
     * @param urlObject
     * 		The object representing the URL. The toString() method of the object will be used
     * 		to get a string representation of the URL.
     * @param headers
     * 		A list of headers to add to the HTTP request. Each header should be a string in the
     * 		same form as one would add to the HTTP request itself: e.g. "someheader: somevalue".
     * @return
     * 		A handle to the PUT {@link HttpMethod} after having been executed.  
     */
    public static HttpMethod doPutOf(final String urlObject, final String... headers) {
        final String url = urlObject.toString();
    	
        final PutMethod method = new PutMethod(url);
        addHeaders(method, headers);        
        
        return callHttpMethod(method);

    }

    /**
     * Executes a HTTP POST request to the URL specified. 
     * 
     * @param urlObject
     * 		The object representing the URL. The toString() method of the object will be used
     * 		to get a string representation of the URL.
     * @param requestBody
     * 		The request entity body.
     * @param contentType
     * 		The request entity content type. NOTE: this value will be set as the "Content-Type"
     * 		header for the request ONLY if the "Content-Type" header is not set specifically in the headers
     * 		parameter. 
     * @param headers
     * 		A list of headers to add to the HTTP request. Each header should be a string in the
     * 		same form as one would add to the HTTP request itself: e.g. "someheader: somevalue".
     * @return
     * 		A handle to the POST {@link HttpMethod} after having been executed.  
     */
    public static HttpMethod doPostOf(final String urlObject, final InputStream requestBody, final String contentType, final String... headers) {
        final String url = urlObject.toString();
    	
        final PostMethod method = new PostMethod(url);
        addHeaders(method, headers);        
        
        method.setRequestEntity(new InputStreamRequestEntity(requestBody, contentType));

        return callHttpMethod(method);

    }

    /**
     * Executes a HTTP DELETE request to the URL specified. 
     * 
     * @param urlObject
     * 		The object representing the URL. The toString() method of the object will be used
     * 		to get a string representation of the URL.
     * @param headers
     * 		A list of headers to add to the HTTP request. Each header should be a string in the
     * 		same form as one would add to the HTTP request itself: e.g. "someheader: somevalue".
     * @return
     * 		A handle to the DELETE {@link HttpMethod} after having been executed.  
     */
    public static HttpMethod doDeleteOf(final String url, final String... headers) {

        final DeleteMethod method = new DeleteMethod(url);
        addHeaders(method, headers);
        
        return callHttpMethod(method);
    }
    
    private static void addHeaders(final HttpMethod method, final String[] headers) {
    	if (headers == null) {
    		return;
    	}
    	
    	for (String header : headers) {
        	final java.util.regex.Matcher matcher = HEADER_PATTERN.matcher(header);
        	
        	if (matcher.matches()) {
        		final String name = matcher.group(1);
        		final String value = matcher.group(2);
        		
        		method.setRequestHeader(name, value);
        	}
    	}
    }
    
    private static HttpMethod callHttpMethod(final HttpMethod method) {

        try {
            client.executeMethod(method);
        } catch (final HttpException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return method;
    }

    public static InputStream getClasspathResourceAsStream(final String xmlClasspathResource) {

        final InputStream xmlContent = AcceptanceTestHelper.class.getResourceAsStream(xmlClasspathResource);

        if (xmlContent == null) {
            throw new RuntimeException(new FileNotFoundException(xmlClasspathResource));
        }

        return xmlContent;

    }

    public static String getClasspathResourceAsString(final String xmlClasspathResource) throws IOException {
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

    public static Node extractXmlNodeFrom(final HttpMethod method) {

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

    public static JsonNode extractJsonNodeFrom(final HttpMethod method) {

        try {
            String responseBody = IOUtils.toString(method.getResponseBodyAsStream());
            return jsonToNode(responseBody);

        } catch (IOException e) {
            throw new RuntimeException("Could not extract JSON object from response body", e);

        }
    }

    // Hamcrest matchers

    public static TypeSafeMatcher<HttpMethod> hasStatusCode(final int expectedStatusCode) {
        return new HasStatusCode(expectedStatusCode);
    }

    public static TypeSafeMatcher<HttpMethod> hasResponseBody(final Matcher<String> bodyMatcher) {
        return new HasResponseBody(bodyMatcher);
    }

    public static TypeSafeMatcher<JsonNode> hasJsonValue(final String fieldName, final Matcher<?> matcher) {
        return new HasJsonValue(fieldName, matcher);
    }

    public static TypeSafeMatcher<JsonNode> hasJsonArray(final String fieldName, final Matcher<?> matcher) {
        return new HasJsonArray(fieldName, matcher);
    }

    public static TypeSafeMatcher<JsonNode> containingValue(final Matcher<?> matcher) {
        return new ContainingValue(matcher);
    }

    public static TypeSafeMatcher<JsonNode> withValueAt(int position, Matcher<?> matcher) {
        return new WithValueAt(position, matcher);
    }

    public static TypeSafeMatcher<JsonNode> withSize(final Matcher<?> matcher) {
        return new WithSize(matcher);
    }

    public static TypeSafeMatcher<JsonNode> valid(final JsonNode schema) {
        return new IsValid(schema);
    }

    public static TypeSafeMatcher<JsonNode> valid(final URL url) {
        return new IsValid(url);
    }

}
