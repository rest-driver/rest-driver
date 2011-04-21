package com.github.restdriver.clientdriver.example;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import org.junit.After;
import org.junit.Before;

/**
 * If you are using the Http Test Bench, you can have your unit tests extend this class which will setup a benchserver &
 * shut it down for you.
 */
public abstract class ClientDriverUnitTest {

    private ClientDriver benchServer;

    /**
     * Starts the bench server. This will be called before your subclass'
     * 
     * @Before -annotated methods.
     */
    @Before
    public final void startClientDriver() {
        benchServer = new ClientDriverFactory().createClientDriver();
    }

    /**
     * Shuts the client driver down, which will also verify that the expectations are correct. This will be called AFTER
     * the @After-annotated methods in your subclass.
     */
    @After
    public final void shutdownClientDriver() {
        benchServer.shutdown();
    }

    /**
     * Get the client driver which has been set up. This will be OK to refer to in your subclass' @Before methods, as
     * the superclass is called first of all.
     * 
     * @return The {@link ClientDriver}
     */
    public final ClientDriver getClientDriver() {
        return benchServer;
    }

}
