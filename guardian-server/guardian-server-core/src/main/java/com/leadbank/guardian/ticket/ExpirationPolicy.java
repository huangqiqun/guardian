package com.leadbank.guardian.ticket;

import java.io.Serializable;

public interface ExpirationPolicy extends Serializable {

    boolean isExpired(TicketState ticketState);
    
    long getTimeToKillInMilliSeconds();
}
