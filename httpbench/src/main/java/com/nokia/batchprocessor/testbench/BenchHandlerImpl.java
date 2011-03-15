package com.nokia.batchprocessor.testbench;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * 
 * Class which acts as a Jetty Handler to see if the actual incoming HTTP request matches any expectation and to act accordingly. In case of any kind
 * of error, {@link BenchServerRuntimeException} is usually thrown.
 * 
 * @author mjg
 * 
 */
public class BenchHandlerImpl extends AbstractHandler implements BenchHandler {

	private final List<BenchRequestResponsePair> expectedResponses;
	private final List<BenchRequestResponsePair> matchedResponses;
	private final RequestMatcher matcher;
	private String unexpectedRequest;

	/**
	 * Constructor which accepts a {@link RequestMatcher}
	 * 
	 * @param matcher
	 *            The {@link RequestMatcher} to use.
	 */
	public BenchHandlerImpl(final RequestMatcher matcher) {

		expectedResponses = new ArrayList<BenchRequestResponsePair>();
		matchedResponses = new ArrayList<BenchRequestResponsePair>();

		this.matcher = matcher;

	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation uses the expected {@link BenchRequest}/ {@link BenchResponse} pairs to serve its requests. If an unexpected request comes
	 * in, a {@link BenchServerRuntimeException} is thrown
	 */
	@Override
	public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

		final BenchRequestResponsePair matchingPair = getMatchingRequestPair(request);
		matchedResponses.add(matchingPair);

		final BenchResponse matchedResponse = matchingPair.getResponse();

		response.setContentType(matchedResponse.getContentType());
		response.setStatus(matchedResponse.getStatus());
		response.getWriter().print(matchedResponse.getContent());

		for (final Entry<String, String> thisHeader : matchedResponse.getHeaders().entrySet()) {
			response.setHeader(thisHeader.getKey(), thisHeader.getValue());
		}

		baseRequest.setHandled(true);

	}

	private BenchRequestResponsePair getMatchingRequestPair(final HttpServletRequest request) {

		int index = 0;

		BenchRequestResponsePair matchedPair = null;
		for (index = 0; index < expectedResponses.size(); index++) {
			final BenchRequestResponsePair thisPair = expectedResponses.get(index);
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
			throw new BenchServerRuntimeException("Unexpected request: " + unexpectedRequest, null);
		}

		expectedResponses.remove(index);

		return matchedPair;
	}

	/**
	 * This method will throw a {@link BenchRuntimeException} if there have been any unexpected requests.
	 */
	@Override
	public void checkForUnexpectedRequests() {

		if (unexpectedRequest != null) {
			throw new BenchRuntimeException("Unexpected request: " + unexpectedRequest, null);
		}

	}

	/**
	 * This method will throw a {@link BenchRuntimeException} if any expectations have not been met.
	 */
	@Override
	public void checkForUnmatchedExpectations() {

		if (!expectedResponses.isEmpty()) {
			throw new BenchRuntimeException(expectedResponses.size() + " unmatched expectation(s), first is: "
					+ expectedResponses.get(0).getRequest(), null);
		}

	}

	/**
	 * Add in a {@link BenchRequest}/{@link BenchResponse} pair.
	 * 
	 * @param request
	 *            The expected request
	 * @param response
	 *            The response to serve to that request
	 * 
	 */
	@Override
	public void addExpectation(final BenchRequest request, final BenchResponse response) {
		expectedResponses.add(new BenchRequestResponsePair(request, response));
	}

	/**
	 * Get this object as a Jetty Handler. Call this if you have a reference to it as a {@link BenchHandler} only.
	 * 
	 * @return "this"
	 */
	@Override
	public Handler getJettyHandler() {
		return this;
	}

}
