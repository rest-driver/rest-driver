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
package com.github.restdriver.clientdriver.exception;

import java.util.List;
import com.github.restdriver.clientdriver.ClientDriverExpectation;
import com.github.restdriver.clientdriver.HttpRealRequest;

import static java.lang.String.format;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * Runtime exception which is thrown when the client driver's expectations fail.
 */
public final class ClientDriverFailedExpectationException extends RuntimeException {

    private static final long serialVersionUID = -2270688849375416363L;
    private static final String EXPECTATION_MESSAGE_TEMPLATE = "%s%n  %s -> %s";

    public ClientDriverFailedExpectationException(List<HttpRealRequest> unexpectedRequests, List<ClientDriverExpectation> expectations) {
        super(createUnexpectedRequestsMessage(unexpectedRequests, expectations));
    }

    public ClientDriverFailedExpectationException(List<ClientDriverExpectation> failedExpectations) {
        super(createFailedExpectationsMessage(failedExpectations));
    }

    private static String createUnexpectedRequestsMessage(List<HttpRealRequest> unexpectedRequests, List<ClientDriverExpectation> expectations) {
        checkArgument(unexpectedRequests != null && !unexpectedRequests.isEmpty(), "unexpectedRequests cannot be empty");

        String message = format("%d unexpected request(s):", unexpectedRequests.size());

        for (HttpRealRequest unexpectedRequest : unexpectedRequests) {
            message = format("%s%n  %s", message, unexpectedRequest);
        }

        if (expectations != null && !expectations.isEmpty()) {
            message = format("%s%n%n%d expectation(s):", message, expectations.size());
            message = addExpectationsMessages(expectations, message);
        }

        return message;
    }

    private static String createFailedExpectationsMessage(List<ClientDriverExpectation> failedExpectations) {
        checkArgument(failedExpectations != null && !failedExpectations.isEmpty(), "failedExpectations cannot be empty");

        String message = format("%d unmatched expectation(s):", failedExpectations.size());

        return addExpectationsMessages(failedExpectations, message);
    }

    private static String addExpectationsMessages(List<ClientDriverExpectation> expectations, String message) {
        for (ClientDriverExpectation expectation : expectations) {
            message = format(EXPECTATION_MESSAGE_TEMPLATE, message, expectation.getStatusString(), expectation.getPair().getRequest());
        }

        return message;
    }
}
