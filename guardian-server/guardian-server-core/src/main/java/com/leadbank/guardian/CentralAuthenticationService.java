package com.leadbank.guardian;

import com.leadbank.guardian.authentication.principal.Credentials;
import com.leadbank.guardian.authentication.principal.Service;
import com.leadbank.guardian.ticket.TicketException;
import com.leadbank.guardian.validation.Assertion;

public interface CentralAuthenticationService {

    String createTicketGrantingTicket(Credentials credentials)
            throws TicketException;

    String grantServiceTicket(String ticketGrantingTicketId, Service service, String sessionName)
            throws TicketException;

    String grantServiceTicket(final String ticketGrantingTicketId,
                              final Service service, final Credentials credentials, final String sessionName)
            throws TicketException;

    Assertion validateServiceTicket(final String serviceTicketId,
                                    final Service service) throws TicketException;

    void destroyTicketGrantingTicket(final String ticketGrantingTicketId);

}
