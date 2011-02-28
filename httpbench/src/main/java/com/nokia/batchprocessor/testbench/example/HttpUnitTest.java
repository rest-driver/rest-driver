package com.nokia.batchprocessor.testbench.example;

import org.junit.After;
import org.junit.Before;

import com.nokia.batchprocessor.testbench.BenchServer;
import com.nokia.batchprocessor.testbench.TestBenchFactory;

/**
 * If you are using the Http Test Bench, you can have your unit tests extend
 * this class which will setup a benchserver & shut it down for you.
 * 
 * @author mjg
 * 
 */
public abstract class HttpUnitTest {

    private BenchServer benchServer;

    /**
     * Starts the bench server. This will be called before your subclass'
     * &064;Before-annotated methods.
     * 
     * @see
     */
    @Before
    public void startBenchServer() {
        benchServer = new TestBenchFactory().createBenchServer();
    }

    /**
     * Shuts the bench server down, which will also verify that the expectations
     * are correct. This will be called AFTER the &064;After-annotated methods
     * in your subclass.
     */
    @After
    public void shutdownBenchServer() {
        benchServer.shutdown();
    }

    /**
     * Get the bench server which has been set up. This will be OK to refer to
     * in your subclass' &064;Before methods, as the superclass is called first
     * of all.
     * 
     * @return The {@link BenchServer}
     */
    public BenchServer getBenchServer() {
        return benchServer;
    }

}
