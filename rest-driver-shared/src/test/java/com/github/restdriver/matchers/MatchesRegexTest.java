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
package com.github.restdriver.matchers;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class MatchesRegexTest {
    
    @Test
    public void successfulWhenUsedWithMatchingPattern() {
        MatchesRegex matchesPattern = new MatchesRegex(Pattern.compile("je[f]{2}"));
        assertThat(matchesPattern.matches("jeff"), is(true));
    }
    
    @Test
    public void failsWhenUsedWithNonMatchingPattern() {
        MatchesRegex matchesPattern = new MatchesRegex(Pattern.compile("je[f]{3}"));
        assertThat(matchesPattern.matches("jeff"), is(false));
    }
    
    @Test
    public void descriptionIsSensible() {
        MatchesRegex matchesPattern = new MatchesRegex(Pattern.compile("je[f]{3}"));
        Description description = new StringDescription();
        matchesPattern.describeTo(description);
        assertThat(description.toString(), is("A string matching the regular expression: je[f]{3}"));
    }
    
    @Test
    public void mismatchDescriptionIsSensible() {
        MatchesRegex matchesPattern = new MatchesRegex(Pattern.compile("je[f]{3}"));
        Description description = new StringDescription();
        matchesPattern.describeMismatch("jeff", description);
        assertThat(description.toString(), is("was \"jeff\""));
    }
    
}
