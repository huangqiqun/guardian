package com.leadbank.guardian.util;

public final class DefaultUniqueTicketIdGenerator implements
    UniqueTicketIdGenerator {

    private final NumericGenerator numericGenerator;

    private final RandomStringGenerator randomStringGenerator;

    private final String suffix;

    public DefaultUniqueTicketIdGenerator() {
        this(null);
    }

    public DefaultUniqueTicketIdGenerator(final int maxLength) {
        this(maxLength, null);
    }

    public DefaultUniqueTicketIdGenerator(final String suffix) {
        this.numericGenerator = new DefaultLongNumericGenerator(1);
        this.randomStringGenerator = new DefaultRandomStringGenerator();

        if (suffix != null) {
            this.suffix = "-" + suffix;
        } else {
            this.suffix = null;
        }
    }

    public DefaultUniqueTicketIdGenerator(final int maxLength,
        final String suffix) {
        this.numericGenerator = new DefaultLongNumericGenerator(1);
        this.randomStringGenerator = new DefaultRandomStringGenerator(maxLength);

        if (suffix != null) {
            this.suffix = "-" + suffix;
        } else {
            this.suffix = null;
        }
    }

    public String getNewTicketId(final String prefix) {
        final String number = this.numericGenerator.getNextNumberAsString();
        final StringBuilder buffer = new StringBuilder(prefix.length() + 2
            + (this.suffix != null ? this.suffix.length() : 0) + this.randomStringGenerator.getMaxLength()
            + number.length());

        buffer.append(prefix);
        buffer.append("-");
        buffer.append(number);
        buffer.append("-");
        buffer.append(this.randomStringGenerator.getNewString());

        if (this.suffix != null) {
            buffer.append(this.suffix);
        }

        return buffer.toString();
    }
}
