package com.leadbank.guardian.ticket.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.leadbank.guardian.ticket.Ticket;
import com.leadbank.guardian.ticket.TicketGrantingTicket;
import com.leadbank.guardian.ticket.registry.RegistryCleaner;
import com.leadbank.guardian.ticket.registry.TicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultTicketRegistryCleaner implements RegistryCleaner {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private TicketRegistry ticketRegistry;

    private LockingStrategy lock = new NoOpLockingStrategy();

    private boolean logUserOutOfServices = true;

    public void clean() {
        this.log.info("Beginning ticket cleanup.");
        this.log.debug("Attempting to acquire ticket cleanup lock.");
        if (!this.lock.acquire()) {
            this.log.info("Could not obtain lock.  Aborting cleanup.");
            return;
        }
        this.log.debug("Acquired lock.  Proceeding with cleanup.");
        try {
            final List<Ticket> ticketsToRemove = new ArrayList<Ticket>();
            final Collection<Ticket> ticketsInCache;
            ticketsInCache = this.ticketRegistry.getTickets();
            for (final Ticket ticket : ticketsInCache) {
                if (ticket.isExpired()) {
                    ticketsToRemove.add(ticket);
                }
            }

            this.log.info(ticketsToRemove.size() + " tickets found to be removed.");
            for (final Ticket ticket : ticketsToRemove) {
                // CAS-686: Expire TGT to trigger single sign-out
                if (this.logUserOutOfServices && ticket instanceof TicketGrantingTicket) {
                    ((TicketGrantingTicket) ticket).expire();
                }
                this.ticketRegistry.deleteTicket(ticket.getId());
            }
        } finally {
            this.log.debug("Releasing ticket cleanup lock.");
            this.lock.release();
        }

        this.log.info("Finished ticket cleanup.");
    }


    public void setTicketRegistry(final TicketRegistry ticketRegistry) {
        this.ticketRegistry = ticketRegistry;
    }


    public void setLock(final LockingStrategy strategy) {
        this.lock = strategy;
    }

    public void setLogUserOutOfServices(final boolean logUserOutOfServices) {
        this.logUserOutOfServices = logUserOutOfServices;
    }
}
