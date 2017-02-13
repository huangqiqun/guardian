package com.leadbank.guardian.ticket;

import com.leadbank.guardian.authentication.Authentication;
import com.leadbank.guardian.authentication.principal.Service;
import org.springframework.util.Assert;

public final class ServiceTicketImpl extends AbstractTicket implements
    ServiceTicket {

    private static final long serialVersionUID = -4223319704861765405L;

    private Service service;

    private boolean fromNewLogin;

    private Boolean grantedTicketAlready = false;
    
    public ServiceTicketImpl() {
        // exists for JPA purposes
    }

    protected ServiceTicketImpl(final String id,
        final TicketGrantingTicketImpl ticket, final Service service,
        final boolean fromNewLogin, final ExpirationPolicy policy) {
        super(id, ticket, policy);

        Assert.notNull(ticket, "ticket cannot be null");
        Assert.notNull(service, "service cannot be null");

        this.service = service;
        this.fromNewLogin = fromNewLogin;
    }

    public boolean isFromNewLogin() {
        return this.fromNewLogin;
    }

    public Service getService() {
        return this.service;
    }

    public boolean isValidFor(final Service serviceToValidate) {
        updateState();
        return serviceToValidate.matches(this.service);
    }

    public TicketGrantingTicket grantTicketGrantingTicket(
        final String id, final Authentication authentication,
        final ExpirationPolicy expirationPolicy) {
        synchronized (this) {
            if(this.grantedTicketAlready) {
                throw new IllegalStateException(
                    "TicketGrantingTicket already generated for this ServiceTicket.  Cannot grant more than one TGT for ServiceTicket");
            }
            this.grantedTicketAlready = true;
        }

        return new TicketGrantingTicketImpl(id, (TicketGrantingTicketImpl) this.getGrantingTicket(),
            authentication, expirationPolicy);
    }
    
    public Authentication getAuthentication() {
        return null;
    }
    
    public final boolean equals(final Object object) {
        if (object == null
            || !(object instanceof ServiceTicket)) {
            return false;
        }

        final Ticket serviceTicket = (Ticket) object;
        
        return serviceTicket.getId().equals(this.getId());
    }
}
