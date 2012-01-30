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
package com.github.restdriver.clientdriver.unit;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.github.restdriver.clientdriver.HttpRealRequest;
import com.github.restdriver.clientdriver.MatchedRequestHandler;
import org.eclipse.jetty.server.Request;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.clientdriver.ClientDriverExpectation;
import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRequestResponsePair;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.exception.ClientDriverInvalidExpectationException;

import javax.servlet.http.HttpServletRequest;

public class ClientDriverExpectationTest {
    
    private static ClientDriverRequest REQUEST = new ClientDriverRequest("/request");
    private static ClientDriverResponse RESPONSE = new ClientDriverResponse();
    private static ClientDriverRequestResponsePair PAIR = new ClientDriverRequestResponsePair(REQUEST, RESPONSE);
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void newlyCreatedExpectationIsExpectedOnceAndNotMatched() {
        ClientDriverExpectation expectation = new ClientDriverExpectation(PAIR);
        assertThat(expectation.getStatusString(), is("expected: 1, actual: 0"));
    }
    
    @Test
    public void newlyCreatedExpectationIsNotSatisfied() {
        ClientDriverExpectation expectation = new ClientDriverExpectation(PAIR);
        assertThat(expectation.isSatisfied(), is(false));
    }
    
    @Test
    public void expectationExpectedANumberOfTimesHasCorrectStatusString() {
        ClientDriverExpectation expectation = new ClientDriverExpectation(PAIR);
        expectation.times(10);
        assertThat(expectation.getStatusString(), is("expected: 10, actual: 0"));
    }
    
    @Test
    public void specifyingNumberOfExpectationsBelowOneThrowsException() {
        thrown.expect(ClientDriverInvalidExpectationException.class);
        thrown.expectMessage("Expectation cannot be matched less than once");
        
        ClientDriverExpectation expectation = new ClientDriverExpectation(PAIR);
        expectation.times(0);
    }
    
    @Test
    public void matchingExpectationSatisfiesIt() {
        ClientDriverExpectation expectation = new ClientDriverExpectation(PAIR);
        expectation.times(3);
        
        assertThat(expectation.isSatisfied(), is(false));

        HttpRealRequest realRequest = mock(HttpRealRequest.class);
        
        expectation.match(realRequest);
        assertThat(expectation.isSatisfied(), is(false));
        expectation.match(realRequest);
        assertThat(expectation.isSatisfied(), is(false));
        expectation.match(realRequest);
        assertThat(expectation.isSatisfied(), is(true));
    }
    
    @Test
    public void matchingExpectationCallsMatcher() {
        MatchedRequestHandler matchHandlerMock = mock(MatchedRequestHandler.class);
        ClientDriverExpectation expectation = new ClientDriverExpectation(PAIR);
        expectation.whenMatched(matchHandlerMock);


        HttpRealRequest realRequest = mock(HttpRealRequest.class);

        expectation.match(realRequest);
        verify(matchHandlerMock).onMatch(realRequest);
    }
    
    @Test
    public void expectationExpectedAnyNumberOfTimesIsMarkedAsSuch() {
        ClientDriverExpectation expectation = new ClientDriverExpectation(PAIR);
        expectation.anyTimes();
        assertThat(expectation.shouldMatchAnyTimes(), is(true));
    }
    
    @Test
    public void expectationExpectedAnyNumberOfTimesIsNotSatisfied() {
        ClientDriverExpectation expectation = new ClientDriverExpectation(PAIR);
        expectation.anyTimes();
        assertThat(expectation.isSatisfied(), is(false));
    }
    
    @Test
    public void expectationExpectedAnyNumberOfTimesHasCorrectStatusString() {
        ClientDriverExpectation expectation = new ClientDriverExpectation(PAIR);
        expectation.anyTimes();
        assertThat(expectation.getStatusString(), is("expected: any, actual: 0"));
    }
    
}
