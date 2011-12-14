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
package com.github.restdriver.clientdriver.jetty;

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

import com.github.restdriver.RestDriverProperties;
import com.github.restdriver.clientdriver.ClientDriverExpectation;
import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequestResponsePair;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.HttpRealRequest;
import com.github.restdriver.clientdriver.RequestMatcher;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.clientdriver.exception.ClientDriverInternalException;

/**
 * Class which acts as a Jetty Handler to see if the actual incoming HTTP request matches any expectation and to act
 * accordingly. In case of any kind of error, {@link ClientDriverInternalException} is usually thrown.
 */
public final class DefaultClientDriverJettyHandler extends AbstractHandler implements ClientDriverJettyHandler {
    
    private static final long DEFAULT_WAIT_INTERVAL = 500;
    
    private final List<ClientDriverExpectation> expectations;
    private final List<ClientDriverRequestResponsePair> matchedResponses;
    private final RequestMatcher matcher;
    private String unexpectedRequest;
    
    /**
     * Constructor which accepts a {@link RequestMatcher}.
     * 
     * @param matcher The {@link RequestMatcher} to use.
     */
    public DefaultClientDriverJettyHandler(RequestMatcher matcher) {
        
        expectations = new ArrayList<ClientDriverExpectation>();
        matchedResponses = new ArrayList<ClientDriverRequestResponsePair>();
        
        this.matcher = matcher;
        
    }
    
    /**
     * {@inheritDoc}
     * <p/>
     * This implementation uses the expected {@link ClientDriverRequest}/ {@link ClientDriverResponse} pairs to serve its requests. If an unexpected request comes in, a
     * {@link com.github.restdriver.clientdriver.exception.ClientDriverInternalException} is thrown
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        ClientDriverRequestResponsePair matchingPair = getMatchingRequestPair(request);
        matchedResponses.add(matchingPair);
        
        ClientDriverResponse matchedResponse = matchingPair.getResponse();
        
        response.setContentType(matchedResponse.getContentType());
        response.setStatus(matchedResponse.getStatus());
        response.getWriter().print(matchedResponse.getContent());
        response.setHeader("Server", "rest-client-driver(" + RestDriverProperties.getVersion() + ")");
        
        for (Entry<String, String> thisHeader : matchedResponse.getHeaders().entrySet()) {
            response.setHeader(thisHeader.getKey(), thisHeader.getValue());
        }
        
        delayIfNecessary(matchingPair.getResponse());
        
        baseRequest.setHandled(true);
    }
    
    private void delayIfNecessary(ClientDriverResponse response) {
        
        if (response.getDelayTime() > 0) {
            
            try {
                response.getDelayTimeUnit().sleep(response.getDelayTime());
                
            } catch (InterruptedException ie) {
                throw new ClientDriverInternalException("Requested delay was interrupted", ie);
            }
            
        }
        
    }
    
    private ClientDriverRequestResponsePair getMatchingRequestPair(HttpServletRequest request) {
        
        int index = 0;
        
        ClientDriverExpectation matchedExpectation = null;
        for (index = 0; index < expectations.size(); index++) {
            ClientDriverExpectation thisExpectation = expectations.get(index);
            ClientDriverRequestResponsePair thisPair = thisExpectation.getPair();
            if (matcher.isMatch(new HttpRealRequest(request), thisPair.getRequest())) {
                thisExpectation.match();
                if (matchedExpectation == null) {
                    matchedExpectation = thisExpectation;
                    break;
                }
            }
        }
        
        if (matchedExpectation == null) {
            unexpectedRequest = request.getMethod() + " " + request.getPathInfo();
            
            String reqQuery = request.getQueryString();
            
            if (reqQuery != null) {
                unexpectedRequest += "?" + reqQuery;
            }
            throw new ClientDriverInternalException("Unexpected request: " + unexpectedRequest, null);
        }
        
        if (matchedExpectation.isSatisfied()) {
            expectations.remove(index);
        }
        
        return matchedExpectation.getPair();
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
        
        if (expectations.isEmpty()) {
            return;
        }
        
        long waitFor = 0;
        ClientDriverExpectation failedExpectation = null;
        
        while (true) {
            
            if (waitFor > 0) {
                waitFor(waitFor);
                waitFor = 0;
            }
        
            for (ClientDriverExpectation expectation : expectations) {
                if (expectation.getPair().getResponse().canExpire() && expectation.getPair().getResponse().hasExpired()) {
                    waitFor = DEFAULT_WAIT_INTERVAL;
                    break;
                }
                
                if (expectation.shouldMatchAnyTimes()) {
                    break;
                }
                
                failedExpectation = expectation;
                
            }
            
            if (waitFor > 0) {
                continue;
            }
            
            if (failedExpectation != null) {
                throw new ClientDriverFailedExpectationException(expectations.size() + " unmatched expectation(s), first is: "
                        + failedExpectation.getPair().getRequest() + failedExpectation.getStatusString(), null);
            }
            
            break;
            
        }
        
    }
    
    private void waitFor(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ie) {
            throw new ClientDriverInternalException("Waiting for requests was interrupted", ie);
        }
    }
    
    /**
     * Add in a {@link ClientDriverRequest}/{@link com.github.restdriver.clientdriver.ClientDriverResponse} pair.
     * 
     * @param request The expected request
     * @param response The response to serve to that request
     * @return The added expectation
     */
    @Override
    public ClientDriverExpectation addExpectation(ClientDriverRequest request, ClientDriverResponse response) {
        ClientDriverRequestResponsePair pair = new ClientDriverRequestResponsePair(request, response);
        ClientDriverExpectation expectation = new ClientDriverExpectation(pair);
        expectations.add(expectation);
        return expectation;
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
