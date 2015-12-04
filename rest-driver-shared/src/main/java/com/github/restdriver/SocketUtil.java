package com.github.restdriver;

import java.io.IOException;
import java.net.ServerSocket;


/**
 * Utility class to retrieve a free port number.
 *
 * @author bichel
 *
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
     */
    public static int getFreePort() throws IOException {
            ServerSocket server = new ServerSocket(0);
            int port = server.getLocalPort();
            server.close();
            return port;
    }

}
