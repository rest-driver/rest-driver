package com.googlecode.rd.clientdriver;

/**
 * Main entry point to the Http Test Bench. Just call
 * <code>new {@link ClientDriverFactory}().{@link #createClientDriver()}</code> to get a {@link ClientDriver} running on a
 * free port. You cannot specify which port, one will be chosen for you.
 */
public class ClientDriverFactory {

    /**
     * Factory method to create and start a {@link ClientDriver}.
     * 
     * @return A new {@link ClientDriver}, which has found a free port, bound to it and started up.
     */
    public ClientDriver createClientDriver() {

        return new ClientDriver(new BenchHandlerImpl(new RequestMatcherImpl()));

    }

}
