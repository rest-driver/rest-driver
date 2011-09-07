package com.github.restdriver.serverdriver;

import java.util.concurrent.TimeUnit;

/**
 * For making asynchronous assertions.
 *
 * <p>
 * For typical use, create an anonymous subclass inline, and implement the {@link #poll()} method:
 *
 * <pre>{@code
       new Poller() {
            public void poll() {
                assertThat(someMethodCall(), is("Success"));
            }
        };
 * }</pre>
 *
 * This assertion will be called a number of times (configurable by using the different constructors).  The first poll is tried
 * immediately, so if you specify 3 attempts with 1 second pause, the total execution time will be about 2 seconds.
 * </p>
 *
 * <p>
 *     This class catches any {@link AssertionError} thrown in the first <em>n-1</em> attempts.
 *     If any other kind of Exception is encountered, or an AssertionError is thrown at the last attempt
 *     then it will be thrown immediately and your test will fail.  All AssertionErrors except the last are swallowed.
 * </p>
 */
public abstract class Poller {

    /**
     * Creates a new Poller set to repeat the {@link #poll()} once per second for ten seconds.
     */
    public Poller() {
        doPolling(10, 1, TimeUnit.SECONDS);
    }

    /**
     * Creates a new Poller set to repeat the {@link #poll()} once per second for the specified number of times.
     *
     * @param times The number of times to poll.
     */
    public Poller(int times) {
        doPolling(times, 1, TimeUnit.SECONDS);
    }

    /**
     * Creates a new Poller set to repeat the {@link #poll()} once per <em>sleepSeconds</em> for the specified number of times.
     *
     * @param times The number of times to try.
     * @param sleepSeconds The number of seconds to sleep between each poll.
     */
    public Poller(int times, long sleepSeconds) {
        doPolling(times, sleepSeconds, TimeUnit.SECONDS);
    }

    /**
     * Creates a new Poller set to repeat the {@link #poll()} once every <em>sleepDuration</em> <em>timeUnits</em> for the specified number of times.
     *
     * @param times The number of times to try.
     * @param sleepDuration The number of time-units to sleep between each poll.
     * @param timeUnits the TimeUnit to use.
     */
    public Poller(int times, long sleepDuration, TimeUnit timeUnits) {
        doPolling(times, sleepDuration, timeUnits);
    }

    private void doPolling(int times, long sleepDuration, TimeUnit timeUnit) {

        for (int remainingAttempts = times - 1; remainingAttempts >= 0; remainingAttempts--) {

            if (remainingAttempts == 0) {
                this.poll();

            } else {
                try {
                    this.poll();
                    return;

                } catch (AssertionError actualError) {
                    // ignore this time.
                }

                sleepSoundly(sleepDuration, timeUnit);
            }
        }
    }

    private void sleepSoundly(long amount, TimeUnit unit) {
        try {
            unit.sleep(amount);
        } catch (InterruptedException ie) {
            throw new RuntimeException("interrupted!", ie);
        }
    }

    /**
     * Override this method with some kind of assertion that will be re-run according to the polling schedule.
     * Any {@link AssertionError} thrown will be swallowed unless this is the last attempt.  Any other
     * kind of Exception will be thrown immediately.
     */
    public abstract void poll();
}
