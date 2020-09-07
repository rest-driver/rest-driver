/*
 *  Copyright Lufthansa Systems.
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

    private MultipartEntityBuilder multipartContent = MultipartEntityBuilder.create();

    /**
     * Adds a form field with a string value.
     * 
     * @param formField Name of the field
     * @param value String value
     */
    public MultipartFormBody addFormField(String formField, String value) {
        multipartContent.addPart(FormBodyPartBuilder.create(formField,
                new StringBody(value, ContentType.TEXT_PLAIN)).build());
        return this;
    }

    /**
     * Adds a form field with a file inside.
     * 
     * @param formField Name of the field
     * @param fileName Name of the file
     * @param content byte contents of the file
     */
    public MultipartFormBody addFileField(String formField, String fileName, byte[] content) {
        multipartContent.addPart(FormBodyPartBuilder.create(formField,
                new ByteArrayBody(content,
                        ContentType.create(URLConnection.guessContentTypeFromName(fileName)),
                        fileName)).
                build());
        return this;
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
