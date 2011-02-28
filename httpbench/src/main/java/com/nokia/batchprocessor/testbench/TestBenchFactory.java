package com.nokia.batchprocessor.testbench;

/**
 * Main entry point to the Http Test Bench. Just call new
 * {@link TestBenchFactory}().{@link #createBenchServer()} to get a
 * {@link BenchServer} running on a free port (you cannot specify which port, it
 * will choose one for you).
 * 
 * @author mjg
 * 
 */
public class TestBenchFactory {

    /**
     * Factory method to create and start a {@link BenchServer}.
     * 
     * @return A new {@link BenchServer}, which has found a free port, bound to
     *         it and started up.
     */
    public BenchServer createBenchServer() {

        return new BenchServer(new BenchHandlerImpl(new RequestMatcherImpl()));

    }

}
