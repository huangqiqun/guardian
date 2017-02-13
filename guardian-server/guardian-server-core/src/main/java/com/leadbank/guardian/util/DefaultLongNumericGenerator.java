package com.leadbank.guardian.util;

import java.util.concurrent.atomic.AtomicLong;

public final class DefaultLongNumericGenerator implements LongNumericGenerator {

    private static final int MAX_STRING_LENGTH = Long.toString(Long.MAX_VALUE)
        .length();

    private static final int MIN_STRING_LENGTH = 1;

    private final AtomicLong count;

    public DefaultLongNumericGenerator() {
        this(0);
        // nothing to do
    }

    public DefaultLongNumericGenerator(final long initialValue) {
        this.count = new AtomicLong(initialValue);
    }

    public long getNextLong() {
        return this.getNextValue();
    }

    public String getNextNumberAsString() {
        return Long.toString(this.getNextValue());
    }

    public int maxLength() {
        return DefaultLongNumericGenerator.MAX_STRING_LENGTH;
    }

    public int minLength() {
        return DefaultLongNumericGenerator.MIN_STRING_LENGTH;
    }

    protected long getNextValue() {
        if (this.count.compareAndSet(Long.MAX_VALUE, 0)) {
            return Long.MAX_VALUE;
        }
        return this.count.getAndIncrement();
    }
}
