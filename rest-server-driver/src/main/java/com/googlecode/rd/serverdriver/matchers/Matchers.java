package com.googlecode.rd.serverdriver.matchers;

import com.googlecode.rd.serverdriver.http.response.Response;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.is;

/**
 * Created by IntelliJ IDEA.
 * User: mjg
 * Date: 21/04/11
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class Matchers {

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

    

}
