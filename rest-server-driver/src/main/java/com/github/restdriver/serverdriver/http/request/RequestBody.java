package com.github.restdriver.serverdriver.http.request;

/**
 * Encapsulates a Request body for a PUT or a POST.
 * 
 * User: mjg
 * Date: 21/04/11
 * Time: 14:32
 */
public final class RequestBody {

    private final String content;
    private final String contentType;

    public RequestBody(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }
}
