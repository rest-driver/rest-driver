package com.github.restdriver.serverdriver.http.request;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 14:32
 *
 * Encapsulates a Request body for a PUT or a POST
 * 
 */
public final class RequestBody {

    final String content;
    final String contentType;

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
