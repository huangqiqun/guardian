package com.leadbank.guardian.ticket;

import com.leadbank.guardian.authentication.Authentication;
import com.leadbank.guardian.authentication.principal.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public final class TicketGrantingTicketImpl extends AbstractTicket implements
    TicketGrantingTicket {

    private static final long serialVersionUID = -5197946718924166491L;

    private static final Logger LOG = LoggerFactory.getLogger(TicketGrantingTicketImpl.class);

    private Authentication authentication;

    private Boolean expired = false;
    
    private final HashMap<String,Service> services = new HashMap<String, Service>();
    
    public TicketGrantingTicketImpl() {
        // nothing to do
    }

    public TicketGrantingTicketImpl(final String id,
        final TicketGrantingTicketImpl ticketGrantingTicket,
        final Authentication authentication, final ExpirationPolicy policy) {
        super(id, ticketGrantingTicket, policy);

        Assert.notNull(authentication, "authentication cannot be null");

        this.authentication = authentication;
    }

    public TicketGrantingTicketImpl(final String id,
        final Authentication authentication, final ExpirationPolicy policy) {
        this(id, null, authentication, policy);
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }

    public synchronized ServiceTicket grantServiceTicket(final String id,
        final Service service, final ExpirationPolicy expirationPolicy,
        final boolean credentialsProvided, final String sessionName) {
        final ServiceTicket serviceTicket = new ServiceTicketImpl(id, this,
            service, this.getCountOfUses() == 0 || credentialsProvided,
            expirationPolicy);

        updateState();
        
        final List<Authentication> authentications = getChainedAuthentications();
        service.setPrincipal(authentications.get(authentications.size()-1).getPrincipal());

        this.services.put(sessionName, service);

        return serviceTicket;
    }
    
    private void logOutOfServices() {
        for (final Entry<String, Service> entry : this.services.entrySet()) {

            if (!entry.getValue().logOutOfService(entry.getKey())) {
                LOG.warn("Logout message not sent to [" + entry.getValue().getId() + "]; Continuing processing...");   
            }
        }
    }
    
    private void logOutOfServicesRedis() {
        for (final Entry<String, Service> entry : this.services.entrySet()) {
            entry.getValue().logOutOfServiceRedis(entry.getKey());
        }
    }

    public boolean isRoot() {
        return this.getGrantingTicket() == null;
    }

    public synchronized void expire() {
        this.expired = true;
        logOutOfServicesRedis();
    }

    public boolean isExpiredInternal() {
        return this.expired;
    }

    public List<Authentication> getChainedAuthentications() {
        final List<Authentication> list = new ArrayList<Authentication>();

        if (this.getGrantingTicket() == null) {
            list.add(this.getAuthentication());
            return Collections.unmodifiableList(list);
        }

        list.add(this.getAuthentication());
        list.addAll(this.getGrantingTicket().getChainedAuthentications());

        return Collections.unmodifiableList(list);
    }
    
    public final boolean equals(final Object object) {
        if (object == null
            || !(object instanceof TicketGrantingTicket)) {
            return false;
        }

        final Ticket ticket = (Ticket) object;
        
        return ticket.getId().equals(this.getId());
    }
}
