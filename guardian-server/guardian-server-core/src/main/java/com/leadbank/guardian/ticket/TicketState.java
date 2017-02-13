package com.leadbank.guardian.ticket;

import com.leadbank.guardian.authentication.Authentication;

public interface TicketState {

    int getCountOfUses();

    long getLastTimeUsed();

    long getPreviousTimeUsed();

    long getCreationTime();

    Authentication getAuthentication();
}
