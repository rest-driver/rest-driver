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
package com.github.restdriver.clientdriver.capture;

import java.util.concurrent.CountDownLatch;

public class LatchBodyCapture<T> implements BodyCapture<T> {
    private CountDownLatch latch;
    private BodyCapture<T> capture;

    public LatchBodyCapture(BodyCapture<T> capture) {
        this(capture, 1);
    }

    public LatchBodyCapture(BodyCapture<T> capture, int times) {
        this.capture = capture;
        this.latch = new CountDownLatch(times);
    }

    @Override
    public T getContent() {
        return capture.getContent();
    }

    @Override
    public void setBody(byte[] content) {
        capture.setBody(content);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
