package com.googlecode.rd.clientdriver;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for classes whose responsibility is to match incoming Http requests with expected BenchRequests
 * 
 * @author mjg
 * 
 */
public interface RequestMatcher {

	/**
	 * Checks for a match between an actual {@link HttpServletRequest} and an expected {@link BenchRequest}. Implementations can be as precise or as
	 * loose as they like when matching.
	 * 
	 * @param actualRequest
	 *            The actual request
	 * @param expectedRequest
	 *            The expected {@link BenchRequest}
	 * @return True if there is a match, falsetto otherwise.
	 */
	boolean isMatch(HttpServletRequest actualRequest, BenchRequest expectedRequest);

}
