package com.leadbank.guardian.ticket;


import com.leadbank.guardian.authentication.principal.Service;

public class TicketValidationException extends TicketException {

    private static final String CODE = "INVALID_SERVICE";
    
    private final Service service;

    public TicketValidationException(final Service service) {
        super(CODE);
        this.service = service;
    }
    
    public Service getOriginalService() {
        return this.service;
    }

}
