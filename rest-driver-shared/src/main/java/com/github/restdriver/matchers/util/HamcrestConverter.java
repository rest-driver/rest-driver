package com.github.restdriver.matchers.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.google.common.base.Function;

public class HamcrestConverter<T, U> {
    private final Function<U, T> converter;

    public HamcrestConverter(Function<U, T> converter) {
        this.converter = converter;
    }

    public TypeSafeMatcher<U> convert(Matcher<T> matcher) {
        return new ConverterMatcher<T, U>(matcher, this.converter);
    }

    private static class ConverterMatcher<V, W> extends TypeSafeMatcher<W> {

        private final Matcher<V> matcher;
        private final Function<W, V> converter;

        public ConverterMatcher(Matcher<V> matcher, Function<W, V> converter) {
            this.matcher = matcher;
            this.converter = converter;
        }

        @Override
        protected boolean matchesSafely(W item) {
            return this.matcher.matches(this.converter.apply(item));
        }

        @Override
        public void describeTo(Description description) {
            this.matcher.describeTo(description);
        }
    }
}