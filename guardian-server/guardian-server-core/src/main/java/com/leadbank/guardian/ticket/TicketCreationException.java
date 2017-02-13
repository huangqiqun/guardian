package com.leadbank.guardian.ticket;

public class TicketCreationException extends TicketException {

    private static final String CODE = "CREATION_ERROR";

    public TicketCreationException() {
        super(CODE);
    }

    public TicketCreationException(final Throwable throwable) {
        super(CODE, throwable);
    }
}
