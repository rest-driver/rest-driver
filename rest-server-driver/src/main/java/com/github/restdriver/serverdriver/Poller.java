package com.github.restdriver.serverdriver;

import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew Gilliard
 * Date: 07/09/11
 * Time: 21:18
 */
public abstract class Poller {

    public Poller() {
        doPolling(10, 1, TimeUnit.SECONDS);
    }

    public Poller(int times) {
        doPolling(times, 1, TimeUnit.SECONDS);
    }

    public Poller(int times, long sleepSeconds) {
        doPolling(times, sleepSeconds, TimeUnit.SECONDS);
    }

    public Poller(int times, long sleepDuration, TimeUnit timeUnit) {
        doPolling(times, sleepDuration, TimeUnit.SECONDS);
    }

    protected void doPolling(int times, long sleepDuration, TimeUnit timeUnit) {

        AssertionError caughtError = null;

        for (int i = times - 1; i >= 0; i--) {

            if (i == 0) {
                this.action();

            } else {

                try {
                    this.action();
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

    public abstract void action();
}
