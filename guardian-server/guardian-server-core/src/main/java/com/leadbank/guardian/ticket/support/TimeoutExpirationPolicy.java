package com.leadbank.guardian.ticket.support;

import com.leadbank.guardian.ticket.ExpirationPolicy;
import com.leadbank.guardian.ticket.TicketState;

import java.util.concurrent.TimeUnit;


public final class TimeoutExpirationPolicy implements ExpirationPolicy {

    private final long timeToKillInMilliSeconds;

    public TimeoutExpirationPolicy(final long timeToKillInSeconds) {
        this.timeToKillInMilliSeconds = TimeUnit.SECONDS.toMillis(timeToKillInSeconds);
    }

    public boolean isExpired(final TicketState ticketState) {
        return (ticketState == null)
            || (System.currentTimeMillis() - ticketState.getLastTimeUsed() >= this.timeToKillInMilliSeconds);
    }

	public long getTimeToKillInMilliSeconds() {
		return timeToKillInMilliSeconds;
	}

}
