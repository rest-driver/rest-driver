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
