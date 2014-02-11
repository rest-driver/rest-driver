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

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.hamcrest.Matcher;

import com.github.restdriver.clientdriver.capture.BodyCapture;
import com.github.restdriver.clientdriver.capture.LatchBodyCapture;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

/**
 * Helper class for fluent creation of Client Driver objects.
 */
public final class RestClientDriver {
    
    private RestClientDriver() {
    }
    
    /**
     * Creates a new {@link ClientDriverRequest} object.
     * 
     * @param path
     *            The path to match
     * @return The newly created request
     */
    public static ClientDriverRequest onRequestTo(Matcher<? extends String> path) {
        return new ClientDriverRequest(path);
    }
    
    /**
     * Creates a new {@link ClientDriverRequest} object.
     * 
     * @param path The path to match
     * @return The newly created request
     */
    public static ClientDriverRequest onRequestTo(String path) {
        return new ClientDriverRequest(path);
    }
    
    /**
     * Creates a new {@link ClientDriverRequest} object.
     * 
     * @param path
     *            The path to match
     * @return The newly created request
     */
    public static ClientDriverRequest onRequestTo(Pattern path) {
        return new ClientDriverRequest(path);
    }
    
    /**
     * Creates a new {@link ClientDriverResponse} object.
     * 
     * @param content
     *            The content to return
     * @return The newly created response
     * @deprecated Use {@link #giveResponse(String, String)} instead.
     */
    @Deprecated
    public static ClientDriverResponse giveResponse(String content) {
        return new ClientDriverResponse(content);
    }
    
    /**
     * Creates a new {@link ClientDriverResponse} object.
     * 
     * @param content
     *            The content to return
     * @param contentType
     *            The content-type of the response
     * @return The newly created response
     */
    public static ClientDriverResponse giveResponse(String content, String contentType) {
        return new ClientDriverResponse(content, contentType);
    }
    
    /**
     * Creates a new {@link ClientDriverResponse} object.
     * 
     * @param content
     *            The content to return
     * @param contentType
     *            The content-type of the response
     * @return The newly created response
     */
    public static ClientDriverResponse giveResponseAsBytes(InputStream content, String contentType) {
        return new ClientDriverResponse(content, contentType);
    }
    
    /**
     * Creates a new {@link ClientDriverResponse} object with no content.
     * 
     * @return The newly created response
     */
    public static ClientDriverResponse giveEmptyResponse() {
        return new ClientDriverResponse();
    }
    
    /**
     * Waits for specified time for populated {@link BodyCapture} object.
     * 
     * @param bodyCapture The capture to wait for.
     * @param time The number of units (given in timeUnit) to wait for.
     * @param timeUnit The unit
     */
    public static void waitFor(BodyCapture<?> bodyCapture, long time, TimeUnit timeUnit) {
        if (bodyCapture instanceof LatchBodyCapture) {
            try {
                ((LatchBodyCapture<?>) bodyCapture).getLatch().await(time,
                        timeUnit);
            } catch (InterruptedException e) {
                throw new ClientDriverFailedExpectationException("Interrupted waiting for capture", e);
            }
        } else {
            long waitUntil = System.currentTimeMillis() + timeUnit.toMillis(time);
            
            while (waitUntil > System.currentTimeMillis()) {
                if (bodyCapture.getContent() != null) {
                    break;
                }
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new ClientDriverFailedExpectationException("Interrupted waiting for capture", e);
                }
            }
        }
    }
    
}
