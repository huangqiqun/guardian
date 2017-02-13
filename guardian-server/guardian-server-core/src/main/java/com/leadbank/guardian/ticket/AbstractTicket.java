package com.leadbank.guardian.ticket;

import org.springframework.util.Assert;

public abstract class AbstractTicket implements Ticket, TicketState {

    // XXX removed final
    private ExpirationPolicy expirationPolicy;

    private String id;

    private TicketGrantingTicketImpl ticketGrantingTicket;

    private long lastTimeUsed;

    private long previousLastTimeUsed;

    private long creationTime;

    private int countOfUses;
    
    protected AbstractTicket() {
        // nothing to do
    }

    public AbstractTicket(final String id, final TicketGrantingTicketImpl ticket,
        final ExpirationPolicy expirationPolicy) {
        Assert.notNull(expirationPolicy, "expirationPolicy cannot be null");
        Assert.notNull(id, "id cannot be null");

        this.id = id;
        this.creationTime = System.currentTimeMillis();
        this.lastTimeUsed = System.currentTimeMillis();
        this.expirationPolicy = expirationPolicy;
        this.ticketGrantingTicket = ticket;
    }

    public final String getId() {
        return this.id;
    }

    protected final void updateState() {
        this.previousLastTimeUsed = this.lastTimeUsed;
        this.lastTimeUsed = System.currentTimeMillis();
        this.countOfUses++;
    }

    public final int getCountOfUses() {
        return this.countOfUses;
    }

    public final long getCreationTime() {
        return this.creationTime;
    }

    public final TicketGrantingTicket getGrantingTicket() {
        return this.ticketGrantingTicket;
    }

    public final long getLastTimeUsed() {
        return this.lastTimeUsed;
    }

    public final long getPreviousTimeUsed() {
        return this.previousLastTimeUsed;
    }

    public final boolean isExpired() {
        return this.expirationPolicy.isExpired(this) || (getGrantingTicket() != null && getGrantingTicket().isExpired()) || isExpiredInternal();
    }
    
    public ExpirationPolicy getExpirationPolicy() {
		return this.expirationPolicy;
	}

	protected boolean isExpiredInternal() {
        return false;
    }

    public final int hashCode() {
        return 34 ^ this.getId().hashCode();
    }

    public final String toString() {
        return this.id;
    }
}
