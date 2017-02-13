package com.leadbank.guardian.ticket.registry;

import com.leadbank.guardian.ticket.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public abstract class AbstractTicketRegistry implements TicketRegistry {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	public final Ticket getTicket(final String ticketId, final Class<? extends Ticket> clazz) {
		Assert.notNull(clazz, "clazz cannot be null");

		final Ticket ticket = this.getTicket(ticketId);

		if (ticket == null) {
			return null;
		}

		if (!clazz.isAssignableFrom(ticket.getClass())) {
			throw new ClassCastException("Ticket [" + ticket.getId() + " is of type " + ticket.getClass() + " when we were expecting " + clazz);
		}

		return ticket;
	}
}