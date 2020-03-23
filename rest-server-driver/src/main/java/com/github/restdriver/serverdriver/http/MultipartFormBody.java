/*
 *  Copyright Lufthansa Systems.
 */
package com.github.restdriver.serverdriver.http;

import java.net.URLConnection;


import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

/**
 * Encapsulates a Request body for a method using multipart form encoded format.
 */
public class MultipartFormBody implements AnyRequestModifier {

    private static final String LINE_FEED = "\r\n";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private String boundary = "xxx" + System.currentTimeMillis() + "xxx";

    private String charset = DEFAULT_CHARSET;

    private MultipartEntityBuilder multipartContent = MultipartEntityBuilder.create();

    /**
     * Adds a form field with a string value.
     * 
     * @param formField Name of the field
     * @param value String value
     */
    public void addFormField(String formField, String value) {
        multipartContent.addPart(FormBodyPartBuilder.create(formField,
                new StringBody(value, ContentType.TEXT_PLAIN)).build());
    }

    /**
     * Adds a form field with a file inside.
     * 
     * @param formField Name of the field
     * @param fileName Name of the file
     * @param content byte contents of the file
     */
    public void addFileField(String formField, String fileName, byte[] content) {
        multipartContent.addPart(FormBodyPartBuilder.create(formField,
                new ByteArrayBody(content,
                        ContentType.create(URLConnection.guessContentTypeFromName(fileName)),
                        fileName)).
                build());
    }

    @Override
    public void applyTo(ServerDriverHttpUriRequest request) {
        HttpUriRequest internalRequest = request.getHttpUriRequest();

        if (!(internalRequest instanceof HttpEntityEnclosingRequest)) {
            return;
        }

        HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) internalRequest;
        entityRequest.setEntity(multipartContent.build());
    }
}
