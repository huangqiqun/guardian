package com.leadbank.guardian.ticket;

public class InvalidTicketException extends TicketException {

    private static final String CODE = "INVALID_TICKET";

    public InvalidTicketException() {
        super(CODE);
    }

    public InvalidTicketException(final Throwable throwable) {
        super(CODE, throwable);
    }
}
