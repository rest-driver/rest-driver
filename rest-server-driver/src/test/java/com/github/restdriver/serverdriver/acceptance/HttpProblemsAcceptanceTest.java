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
package com.github.restdriver.serverdriver.acceptance;

import static com.github.restdriver.serverdriver.RestServerDriver.*;

import java.io.IOException;

import org.junit.Test;

import com.github.restdriver.SocketUtil;
import com.github.restdriver.serverdriver.http.exception.RuntimeClientProtocolException;
import com.github.restdriver.serverdriver.http.exception.RuntimeConnectException;
import com.github.restdriver.serverdriver.http.exception.RuntimeUnknownHostException;

/**
 * User: mjg
 * Date: 21/04/11
 * Time: 13:52
 */
public class HttpProblemsAcceptanceTest {
    
    @Test(expected = RuntimeUnknownHostException.class)
    public void getWithNoSuchHostThrowsException() {
        get("http://no-such-host");
    }
    
    @Test(expected = RuntimeClientProtocolException.class)
    public void getWithInvalidProtocolThrowsException() {
        get("xyzzz://no-such-host");
    }
    
    @Test(expected = RuntimeConnectException.class)
    public void noServerListeningThrowsException() throws IOException {
        get("http://localhost:" + SocketUtil.getFreePort());
    }
    
}
