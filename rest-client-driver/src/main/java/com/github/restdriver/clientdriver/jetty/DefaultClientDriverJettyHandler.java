package com.github.restdriver.clientdriver.jetty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.restdriver.clientdriver.ClientDriverRequestResponsePair;
import com.github.restdriver.clientdriver.RequestMatcher;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.clientdriver.exception.ClientDriverInternalException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.github.restdriver.types.ClientDriverRequest;
import com.github.restdriver.types.ClientDriverResponse;

/**
 * Class which acts as a Jetty Handler to see if the actual incoming HTTP request matches any expectation and to act
 * accordingly. In case of any kind of error, {@link ClientDriverInternalException} is usually thrown.
 */
public class DefaultClientDriverJettyHandler extends AbstractHandler implements ClientDriverJettyHandler {

    private final List<ClientDriverRequestResponsePair> expectedResponses;
    private final List<ClientDriverRequestResponsePair> matchedResponses;
    private final RequestMatcher matcher;
    private String unexpectedRequest;

    /**
     * Constructor which accepts a {@link RequestMatcher}
     * 
     * @param matcher
     *            The {@link RequestMatcher} to use.
     */
    public DefaultClientDriverJettyHandler(final RequestMatcher matcher) {

        expectedResponses = new ArrayList<ClientDriverRequestResponsePair>();
        matchedResponses = new ArrayList<ClientDriverRequestResponsePair>();

        this.matcher = matcher;

    }

    /**
     * {@inheritDoc}
     * 
     * This implementation uses the expected {@link ClientDriverRequest}/ {@link ClientDriverResponse} pairs to serve its requests. If
     * an unexpected request comes in, a {@link com.github.restdriver.clientdriver.exception.ClientDriverInternalException} is thrown
     */
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        final ClientDriverRequestResponsePair matchingPair = getMatchingRequestPair(request);
        matchedResponses.add(matchingPair);

        final ClientDriverResponse matchedResponse = matchingPair.getResponse();

        response.setContentType(matchedResponse.getContentType());
        response.setStatus(matchedResponse.getStatus());
        response.getWriter().print(matchedResponse.getContent());

        for (final Entry<String, String> thisHeader : matchedResponse.getHeaders().entrySet()) {
            response.setHeader(thisHeader.getKey(), thisHeader.getValue());
        }

        baseRequest.setHandled(true);

    }

    private ClientDriverRequestResponsePair getMatchingRequestPair(final HttpServletRequest request) {

        int index = 0;

        ClientDriverRequestResponsePair matchedPair = null;
        for (index = 0; index < expectedResponses.size(); index++) {
            final ClientDriverRequestResponsePair thisPair = expectedResponses.get(index);
            if (matcher.isMatch(request, thisPair.getRequest())) {
                if (matchedPair == null) {
                    matchedPair = thisPair;
                    break;
                }
            }
        }

        if (matchedPair == null) {
            unexpectedRequest = request.getPathInfo();

            final String reqQuery = request.getQueryString();

            if (reqQuery != null) {
                unexpectedRequest += "?" + reqQuery;
            }
            throw new ClientDriverInternalException("Unexpected request: " + unexpectedRequest, null);
        }

        expectedResponses.remove(index);

        return matchedPair;
    }

    /**
     * This method will throw a {@link ClientDriverFailedExpectationException} if there have been any unexpected requests.
     */
    @Override
    public void checkForUnexpectedRequests() {

        if (unexpectedRequest != null) {
            throw new ClientDriverFailedExpectationException("Unexpected request: " + unexpectedRequest, null);
        }

    }

    /**
     * This method will throw a {@link ClientDriverFailedExpectationException} if any expectations have not been met.
     */
    @Override
    public void checkForUnmatchedExpectations() {

        if (!expectedResponses.isEmpty()) {
            throw new ClientDriverFailedExpectationException(expectedResponses.size() + " unmatched expectation(s), first is: "
                    + expectedResponses.get(0).getRequest(), null);
        }

    }

    /**
     * Add in a {@link ClientDriverRequest}/{@link ClientDriverResponse} pair.
     * 
     * @param request
     *            The expected request
     * @param response
     *            The response to serve to that request
     * 
     */
    @Override
    public void addExpectation(final ClientDriverRequest request, final ClientDriverResponse response) {
        expectedResponses.add(new ClientDriverRequestResponsePair(request, response));
    }

    /**
     * Get this object as a Jetty Handler. Call this if you have a reference to it as a {@link ClientDriverJettyHandler} only.
     * 
     * @return "this"
     */
    @Override
    public Handler getJettyHandler() {
        return this;
    }

}
