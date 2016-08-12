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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.restdriver.clientdriver.ClientDriverExpectation;
import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequestResponsePair;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.HttpRealRequest;
import com.github.restdriver.clientdriver.RequestMatcher;
import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;
import com.github.restdriver.clientdriver.exception.ClientDriverInternalException;
import com.google.common.collect.Lists;

/**
 * Class which acts as a Jetty Handler to see if the actual incoming HTTP
 * request matches any expectation and to act accordingly. In case of any kind
 * of error, {@link ClientDriverInternalException} is usually thrown.
 */
public final class DefaultClientDriverJettyHandler extends AbstractHandler implements ClientDriverJettyHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClientDriverJettyHandler.class);
    private static final long DEFAULT_WAIT_INTERVAL = 500;
    
    private final List<ClientDriverExpectation> expectations;
    private final List<ClientDriverRequestResponsePair> matchedResponses;
    private final RequestMatcher matcher;
    private final List<HttpRealRequest> unexpectedRequests;
    private final List<HttpRealRequest> requests;
    private boolean failFastOnUnexpectedRequest = true;
    
    /**
     * Constructor which accepts a {@link RequestMatcher}.
     * 
     * @param matcher
     *            The {@link RequestMatcher} to use.
     */
    public DefaultClientDriverJettyHandler(RequestMatcher matcher) {
        
        expectations = new ArrayList<ClientDriverExpectation>();
        matchedResponses = new ArrayList<ClientDriverRequestResponsePair>();
        unexpectedRequests = new ArrayList<HttpRealRequest>();
        requests = new ArrayList<HttpRealRequest>();
        
        this.matcher = matcher;
        
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation uses the expected {@link ClientDriverRequest}/ {@link ClientDriverResponse} pairs to serve its requests. If an unexpected request comes in, a
     * {@link ClientDriverInternalException} is thrown.</p>
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        LOGGER.info("Handling: {} {}", request.getMethod(), request.getPathInfo());
        
        ClientDriverRequestResponsePair matchingPair = getMatchingRequestPair(request);
        
        if (matchingPair != null) {
            matchedResponses.add(matchingPair);
            
            matchingPair.getResponse().handle(response);
        } else {
            response.setStatus(404);
        }
        
        baseRequest.setHandled(true);
    }

    private synchronized ClientDriverRequestResponsePair getMatchingRequestPair(HttpServletRequest request) {
        
        ClientDriverExpectation matchedExpectation = null;
        HttpRealRequest realRequest = new HttpRealRequest(request);
        requests.add(realRequest);

        int index;
        for (index = 0; index < expectations.size(); index++) {
            ClientDriverExpectation thisExpectation = expectations.get(index);
            ClientDriverRequestResponsePair thisPair = thisExpectation.getPair();
            
            if (matcher.isMatch(realRequest, thisPair.getRequest())) {
                thisExpectation.match(realRequest);
                matchedExpectation = thisExpectation;
                break;
            }
        }
        
        if (matchedExpectation == null) {
            this.unexpectedRequests.add(realRequest);
            
            if (failFastOnUnexpectedRequest) {
                throw new ClientDriverFailedExpectationException(unexpectedRequests, expectations);
            } else {
                return null;
            }
        } else {
            if (matchedExpectation.isSatisfied()) {
                expectations.remove(index);
            }
            
            captureBodyIfRequired(realRequest, matchedExpectation);
            
            return matchedExpectation.getPair();
        }
    }
    
    private void captureBodyIfRequired(HttpRealRequest realRequest,
            ClientDriverExpectation matchedExpectation) {
        ClientDriverRequest request = matchedExpectation.getPair().getRequest();
        if (request.getBodyCapture() != null) {
            request.getBodyCapture().setBody(realRequest.getBodyContent());
        }
    }
    
    @Override
    public void checkForUnexpectedRequests() {
        
        if (!unexpectedRequests.isEmpty()) {
            throw new ClientDriverFailedExpectationException(unexpectedRequests, expectations);
        }
        
    }
    
    @Override
    public void checkForUnmatchedExpectations() {
        
        if (expectations.isEmpty()) {
            return;
        }
        
        long period = 0;
        List<ClientDriverExpectation> failedExpectations = Lists.newArrayList();
        
        while (true) {
            
            if (period > 0) {
                waitFor(period);
                period = 0;
            }
            
            for (ClientDriverExpectation expectation : expectations) {
                
                if (expectation.shouldMatchAnyTimes()) {
                    continue;
                }
                
                ClientDriverResponse response = expectation.getPair().getResponse();
                
                if (response.canExpire() && response.hasNotExpired()) {
                    period = DEFAULT_WAIT_INTERVAL;
                    break;
                }
                
                failedExpectations.add(expectation);
            }
            
            if (period > 0) {
                continue;
            }
            
            if (!failedExpectations.isEmpty()) {
                throw new ClientDriverFailedExpectationException(failedExpectations);
            }
            
            break;
        }
    }
    
    @Override
    public void noFailFastOnUnexpectedRequest() {
        failFastOnUnexpectedRequest = false;
    }
    
    @Override
    public void reset() {
        expectations.clear();
        matchedResponses.clear();
        unexpectedRequests.clear();
        failFastOnUnexpectedRequest = true;
    }
    
    private void waitFor(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ie) {
            throw new ClientDriverInternalException("Waiting for requests was interrupted", ie);
        }
    }
    
    /**
     * Add in a {@link ClientDriverRequest}/ {@link com.github.restdriver.clientdriver.ClientDriverResponse} pair.
     * 
     * @param request
     *            The expected request
     * @param response
     *            The response to serve to that request
     * @return The added expectation
     */
    @Override
    public ClientDriverExpectation addExpectation(ClientDriverRequest request, ClientDriverResponse response) {
        ClientDriverRequestResponsePair pair = new ClientDriverRequestResponsePair(request, response);
        ClientDriverExpectation expectation = new ClientDriverExpectation(pair);
        expectations.add(expectation);
        return expectation;
    }

    @Override
    public void verify(ClientDriverRequest request, int times) {
        int count = 0;
        for (HttpRealRequest realRequest : requests) {
            if (matcher.isMatch(realRequest, request)) {
                count++;
            }
        }

        if (count != times) {
            throw new RuntimeException(String.format("Incorrect request times: %s", request));
        }
    }
}
