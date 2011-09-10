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

import java.util.concurrent.TimeUnit;

/**
 * Immutable value object that encapsulates a duration of time.
 * Stores a duration amount and a {@link java.util.concurrent.TimeUnit}.
 */
public final class TimeDuration {

    private final long duration;
    private final TimeUnit timeUnit;

    /**
     * Constructor.
     *
     * @param duration how many?
     * @param timeUnit how many what?
     */
    public TimeDuration(long duration, TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    /**
     * @return How long?
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @return The units which to interpret {@link #getDuration()}
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
    
}
