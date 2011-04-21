package com.github.restdriver.clientdriver;

import javax.servlet.http.HttpServletRequest;

import com.github.restdriver.types.ClientDriverRequest;

/**
 * Interface for classes whose responsibility is to match incoming Http requests with expected BenchRequests
 * 
 * @author mjg
 * 
 */
public interface RequestMatcher {

	/**
	 * Checks for a match between an actual {@link HttpServletRequest} and an expected {@link ClientDriverRequest}.
     * Implementations can be as precise or as loose as they like when matching.
	 * 
	 * @param actualRequest
	 *            The actual request
	 * @param expectedRequest
	 *            The expected {@link ClientDriverRequest}
	 * @return True if there is a match, falsetto otherwise.
	 */
	boolean isMatch(HttpServletRequest actualRequest, ClientDriverRequest expectedRequest);

}
