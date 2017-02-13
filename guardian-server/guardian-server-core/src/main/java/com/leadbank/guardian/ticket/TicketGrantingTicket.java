package com.leadbank.guardian.ticket;

import com.leadbank.guardian.authentication.Authentication;
import com.leadbank.guardian.authentication.principal.Service;

import java.util.List;

public interface TicketGrantingTicket extends Ticket {

	String PREFIX = "TGT";

	Authentication getAuthentication();

	ServiceTicket grantServiceTicket(String id, Service service, ExpirationPolicy expirationPolicy, boolean credentialsProvided, String sid);

	void expire();

	boolean isRoot();

	List<Authentication> getChainedAuthentications();
}
