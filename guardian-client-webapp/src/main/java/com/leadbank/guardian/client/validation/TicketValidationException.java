package com.leadbank.guardian.client.validation;

public class TicketValidationException extends Exception {

    public TicketValidationException(final String string) {
        super(string);
    }

    public TicketValidationException(final String string, final Throwable throwable) {
        super(string, throwable);
    }

    public TicketValidationException(final Throwable throwable) {
        super(throwable);
    }
}
