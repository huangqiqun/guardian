package com.leadbank.guardian.ticket.registry;

import com.leadbank.guardian.ticket.Ticket;

import java.util.Collection;

public interface TicketRegistry {

    void addTicket(Ticket ticket);

    Ticket getTicket(String ticketId, Class<? extends Ticket> clazz);

    Ticket getTicket(String ticketId);

    boolean deleteTicket(String ticketId);

    Collection<Ticket> getTickets();
}
