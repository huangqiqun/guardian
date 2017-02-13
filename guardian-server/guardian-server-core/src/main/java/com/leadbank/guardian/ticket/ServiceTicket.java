package com.leadbank.guardian.ticket;

import com.leadbank.guardian.authentication.Authentication;
import com.leadbank.guardian.authentication.principal.Service;

public interface ServiceTicket extends Ticket {

	String PREFIX = "ST";

	Service getService();

	boolean isFromNewLogin();

	boolean isValidFor(Service service);

	TicketGrantingTicket grantTicketGrantingTicket(String id, Authentication authentication, ExpirationPolicy expirationPolicy);
}
