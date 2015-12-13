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
package com.github.restdriver;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Utility class to retrieve a free port number.
 *
 * @author bichel
 */
public final class SocketUtil {
    
    private SocketUtil() {
    }
    
    /**
     * Gets a free port on localhost for binding to.
     *
     * @see "http://chaoticjava.com/posts/retrieving-a-free-port-for-socket-binding/"
     *
     * @return The port number.
     * 
     * @throws IOException When there is a failure getting a free port
     */
    public static int getFreePort() throws IOException {
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();
        return port;
    }
    
}
