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
package com.github.restdriver.serverdriver.polling;

import com.github.restdriver.serverdriver.http.exception.RuntimeInterruptedException;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * For making asynchronous assertions.
 * <p/>
 * <p>
 * For typical use, create an anonymous subclass inline, and implement the {@link #poll()} method:
 * <p/>
 * <pre>{@code
 * new Poller( times(10), every(100, MILLISECONDS) ) {
 *     public void poll() {
 *         assertThat(someMethodCall(), is("Success"));
 *     }
 * };
 * }</pre>
 * <p/>
 * This assertion will be called a number of times (configurable by using the different constructors).  The first poll is tried
 * immediately, so if you specify 3 attempts with 1 second pause, the total execution time will be about 2 seconds.
 * </p>
 * <p/>
 * <p>
 * This class catches any {@link AssertionError} thrown in the first <em>n-1</em> attempts.
 * If any other kind of Exception is encountered, or an AssertionError is thrown at the last attempt
 * then it will be thrown immediately and your test will fail.  All AssertionErrors except the last are swallowed.
 * </p>
 * <p>
 * For debugging purposes you may call {@link #loudly()} in your poll method, and interim AssertionErrors will be logged
 * to System.out rather than being simply ignored.
 * </p>
 */
public abstract class Poller {

    private static final int DEFAULT_ATTEMPTS = 10;

    private static final TimeDuration DEFAULT_POLL = new TimeDuration(1, SECONDS);

    private boolean loud;

    /**
     * Creates a new Poller set to repeat the {@link #poll()} once per second for ten seconds.
     */
    public Poller() {
        doPolling(DEFAULT_ATTEMPTS, DEFAULT_POLL);
    }

    /**
     * Creates a new Poller set to repeat the {@link #poll()} once per second for the specified number of times.
     *
     * @param times The number of times to poll.
     */
    public Poller(int times) {
        doPolling(times, DEFAULT_POLL);
    }

    /**
     * Creates a new Poller set to repeat the {@link #poll()} once every <em>sleepDuration</em> <em>timeUnits</em> for the specified number of times.
     *
     * @param times         The number of times to try.
     * @param pollPeriod The duration of the sleep between each poll.
     */
    public Poller(int times, TimeDuration pollPeriod) {
        doPolling(times, pollPeriod);
    }

    private void doPolling(int times, TimeDuration pollPeriod) {

        for (int remainingAttempts = times - 1; remainingAttempts >= 0; remainingAttempts--) {

            if (remainingAttempts == 0) {
                this.poll();

            } else {
                try {
                    this.poll();
                    return;

                } catch (AssertionError intermediateError) {

                    if (loud) {
                        System.out.println("remainingAttempts=" + remainingAttempts + ", caught AssertionError: " + intermediateError.getMessage());
                    }

                }

                sleepSoundly(pollPeriod);
            }
        }
    }

    private void sleepSoundly(TimeDuration pollPeriod) {
        try {
            pollPeriod.getTimeUnit().sleep(pollPeriod.getDuration());
        } catch (InterruptedException ie) {
            throw new RuntimeInterruptedException("interrupted!", ie);
        }
    }

    /**
     * Call this method in your {@link #poll()} implementation (before any assertions)
     * to enable logging of intermediate assertions to System.out.
     */
    protected final void loudly() {
        this.loud = true;
    }

    /**
     * Override this method with some kind of assertion that will be re-run according to the polling schedule.
     * Any {@link AssertionError} thrown will be swallowed unless this is the last attempt.  Any other
     * kind of Exception will be thrown immediately.
     * <br/>
     * For debugging, intermediate AssertionErrors can be logged to sysout by calling {@link #loudly} in this method.
     */
    public abstract void poll();

    /**
     * For helping you define your fluent interface.  This method simply returns its argument but allows
     * calls like {@code new Poller( times(4) )} then it is clear what the 4 refers to, rather than just saying
     * {@code new Poller(4)} which is ambiguous.  4 what?  elephants?
     *
     * @param count a number
     * @return count
     */
    public static int times(int count) {
        return count;
    }

    /**
     * For helping your fluent interface, this simply creates a {@link TimeDuration} object in a more natural way.
     * For an example of usage, see the documentation for {@link Poller}.
     *
     * @param duration the duration
     * @param unit the TimeUnit
     * @return the new {@link TimeDuration}.
     */
    public static TimeDuration every(long duration, TimeUnit unit) {
        return new TimeDuration(duration, unit);
    }
}
