package com.leadbank.guardian;

import com.leadbank.guardian.authentication.Authentication;
import com.leadbank.guardian.authentication.AuthenticationManager;
import com.leadbank.guardian.authentication.MutableAuthentication;
import com.leadbank.guardian.authentication.handler.AuthenticationException;
import com.leadbank.guardian.authentication.principal.Credentials;
import com.leadbank.guardian.authentication.principal.Principal;
import com.leadbank.guardian.authentication.principal.Service;
import com.leadbank.guardian.authentication.principal.SimplePrincipal;
import com.leadbank.guardian.services.RegisteredService;
import com.leadbank.guardian.services.ServicesManager;
import com.leadbank.guardian.services.UnauthorizedServiceException;
import com.leadbank.guardian.services.UnauthorizedSsoServiceException;
import com.leadbank.guardian.ticket.*;
import com.leadbank.guardian.ticket.registry.TicketRegistry;
import com.leadbank.guardian.util.UniqueTicketIdGenerator;
import com.leadbank.guardian.validation.Assertion;
import com.leadbank.guardian.validation.ImmutableAssertionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CentralAuthenticationServiceImpl implements CentralAuthenticationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private TicketRegistry ticketRegistry;

    private TicketRegistry serviceTicketRegistry;

    private AuthenticationManager authenticationManager;

    private UniqueTicketIdGenerator ticketGrantingTicketUniqueTicketIdGenerator;

    private Map<String, UniqueTicketIdGenerator> uniqueTicketIdGeneratorsForService;

    private ExpirationPolicy ticketGrantingTicketExpirationPolicy;

    private ExpirationPolicy serviceTicketExpirationPolicy;

    private ServicesManager servicesManager;

    public void destroyTicketGrantingTicket(final String ticketGrantingTicketId) {
        Assert.notNull(ticketGrantingTicketId);

        if (log.isDebugEnabled()) {
            log.debug("Removing ticket [" + ticketGrantingTicketId + "] from registry.");
        }
        final TicketGrantingTicket ticket = (TicketGrantingTicket) this.ticketRegistry.getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);

        if (ticket == null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Ticket found.  Expiring and then deleting.");
        }
        ticket.expire();
        this.ticketRegistry.deleteTicket(ticketGrantingTicketId);
    }

    public String grantServiceTicket(final String ticketGrantingTicketId, final Service service, final Credentials credentials, final String sessionName) throws TicketException {

        Assert.notNull(ticketGrantingTicketId, "ticketGrantingticketId cannot be null");
        Assert.notNull(service, "service cannot be null");

        final TicketGrantingTicket ticketGrantingTicket;
        ticketGrantingTicket = (TicketGrantingTicket) this.ticketRegistry.getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);

        if (ticketGrantingTicket == null) {
            throw new InvalidTicketException();
        }

        synchronized (ticketGrantingTicket) {
            if (ticketGrantingTicket.isExpired()) {
                this.ticketRegistry.deleteTicket(ticketGrantingTicketId);
                throw new InvalidTicketException();
            }
        }

        final RegisteredService registeredService = this.servicesManager
            .findServiceBy(service);

        if (registeredService == null || !registeredService.isEnabled()) {
            log.warn("ServiceManagement: Unauthorized Service Access. Service [" + service.getId() + "] not found in Service Registry.");
            throw new UnauthorizedServiceException();
        }

        if (!registeredService.isSsoEnabled() && credentials == null
            && ticketGrantingTicket.getCountOfUses() > 0) {
            log.warn("ServiceManagement: Service Not Allowed to use SSO.  Service [" + service.getId() + "]");
            throw new UnauthorizedSsoServiceException();
        }

        if (credentials != null) {
            try {
                final Authentication authentication = this.authenticationManager
                    .authenticate(credentials);
                final Authentication originalAuthentication = ticketGrantingTicket.getAuthentication();

                if (!(authentication.getPrincipal().equals(originalAuthentication.getPrincipal()) && authentication.getAttributes().equals(originalAuthentication.getAttributes()))) {
                    throw new TicketCreationException();
                }
            } catch (final AuthenticationException e) {
                throw new TicketCreationException(e);
            }
        }

        // this code is a bit brittle by depending on the class name.  Future versions (i.e. CAS4 will know inherently how to identify themselves)
        final UniqueTicketIdGenerator serviceTicketUniqueTicketIdGenerator = this.uniqueTicketIdGeneratorsForService
            .get(service.getClass().getName());

        final ServiceTicket serviceTicket = ticketGrantingTicket
            .grantServiceTicket(serviceTicketUniqueTicketIdGenerator
                .getNewTicketId(ServiceTicket.PREFIX), service,
                this.serviceTicketExpirationPolicy, credentials != null, sessionName);

        this.serviceTicketRegistry.addTicket(ticketGrantingTicket);
        this.serviceTicketRegistry.addTicket(serviceTicket);

        if (log.isInfoEnabled()) {
            final List<Authentication> authentications = serviceTicket.getGrantingTicket().getChainedAuthentications();
            final String formatString = "Granted %s ticket [%s] for service [%s] for user [%s]";
            final String type;
            final String principalId = authentications.get(authentications.size()-1).getPrincipal().getId();

            if (authentications.size() == 1) {
                type = "service";

            } else {
                type = "proxy";
            }

            log.info(String.format(formatString, type, serviceTicket.getId(), service.getId(), principalId));
        }

        return serviceTicket.getId();
    }

    public String grantServiceTicket(final String ticketGrantingTicketId,
        final Service service, final String sessionName) throws TicketException {
        return this.grantServiceTicket(ticketGrantingTicketId, service, null, sessionName);
    }

    public Assertion validateServiceTicket(final String serviceTicketId, final Service service) throws TicketException {
        Assert.notNull(serviceTicketId, "serviceTicketId cannot be null");
        Assert.notNull(service, "service cannot be null");

        final ServiceTicket serviceTicket = (ServiceTicket) this.serviceTicketRegistry.getTicket(serviceTicketId, ServiceTicket.class);

        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);

        if (registeredService == null || !registeredService.isEnabled()) {
            log.warn("ServiceManagement: Service does not exist is not enabled, and thus not allowed to validate tickets.   Service: [" + service.getId() + "]");
            throw new UnauthorizedServiceException("Service not allowed to validate tickets.");
        }

        if (serviceTicket == null) {
            log.info("ServiceTicket [" + serviceTicketId + "] does not exist.");
            throw new InvalidTicketException();
        }

        try {
            synchronized (serviceTicket) {
                if (serviceTicket.isExpired()) {
                    log.info("ServiceTicket [" + serviceTicketId + "] has expired.");
                    throw new InvalidTicketException();
                }

                if (!serviceTicket.isValidFor(service)) {
                    log.error("ServiceTicket [" + serviceTicketId + "] with service [" + serviceTicket.getService().getId() + " does not match supplied service [" + service + "]");
                    throw new TicketValidationException(serviceTicket.getService());
                }
            }

            final int authenticationChainSize = serviceTicket
                .getGrantingTicket().getChainedAuthentications().size();
            final Authentication authentication = serviceTicket
                .getGrantingTicket().getChainedAuthentications().get(
                    authenticationChainSize - 1);
            final Principal principal = authentication.getPrincipal();
            final String principalId = principal.getId();

            final Authentication authToUse;

            if (!registeredService.isIgnoreAttributes()) {
                final Map<String, Object> attributes = new HashMap<String, Object>();

                attributes.putAll(principal.getAttributes());

                final Principal modifiedPrincipal = new SimplePrincipal(
                    principalId, attributes);
                final MutableAuthentication mutableAuthentication = new MutableAuthentication(
                    modifiedPrincipal, authentication.getAuthenticatedDate());
                mutableAuthentication.getAttributes().putAll(
                    authentication.getAttributes());
                mutableAuthentication.getAuthenticatedDate().setTime(
                    authentication.getAuthenticatedDate().getTime());
                authToUse = mutableAuthentication;
            } else {
                authToUse = authentication;
            }


            final List<Authentication> authentications = new ArrayList<Authentication>();

            for (int i = 0; i < authenticationChainSize - 1; i++) {
                authentications.add(serviceTicket.getGrantingTicket().getChainedAuthentications().get(i));
            }
            authentications.add(authToUse);

            return new ImmutableAssertionImpl(authentications, serviceTicket.getService(), serviceTicket.isFromNewLogin());
        } finally {
            if (serviceTicket.isExpired()) {
                this.serviceTicketRegistry.deleteTicket(serviceTicketId);
            }
        }
    }

    public String createTicketGrantingTicket(final Credentials credentials) throws TicketCreationException {
        Assert.notNull(credentials, "credentials cannot be null");

        try {
            final Authentication authentication = this.authenticationManager
                .authenticate(credentials);

            final TicketGrantingTicket ticketGrantingTicket = new TicketGrantingTicketImpl(
                this.ticketGrantingTicketUniqueTicketIdGenerator
                    .getNewTicketId(TicketGrantingTicket.PREFIX),
                authentication, this.ticketGrantingTicketExpirationPolicy);

            this.ticketRegistry.addTicket(ticketGrantingTicket);
            return ticketGrantingTicket.getId();
        } catch (final AuthenticationException e) {
            throw new TicketCreationException(e);
        }
    }

    public void setTicketRegistry(final TicketRegistry ticketRegistry) {
        this.ticketRegistry = ticketRegistry;

        if (this.serviceTicketRegistry == null) {
            this.serviceTicketRegistry = ticketRegistry;
        }
    }

    public void setServiceTicketRegistry(final TicketRegistry serviceTicketRegistry) {
        this.serviceTicketRegistry = serviceTicketRegistry;
    }

    public void setAuthenticationManager(
        final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setTicketGrantingTicketExpirationPolicy(
        final ExpirationPolicy ticketGrantingTicketExpirationPolicy) {
        this.ticketGrantingTicketExpirationPolicy = ticketGrantingTicketExpirationPolicy;
    }

    public void setTicketGrantingTicketUniqueTicketIdGenerator(
        final UniqueTicketIdGenerator uniqueTicketIdGenerator) {
        this.ticketGrantingTicketUniqueTicketIdGenerator = uniqueTicketIdGenerator;
    }

    public void setServiceTicketExpirationPolicy(
        final ExpirationPolicy serviceTicketExpirationPolicy) {
        this.serviceTicketExpirationPolicy = serviceTicketExpirationPolicy;
    }

    public void setUniqueTicketIdGeneratorsForService(
        final Map<String, UniqueTicketIdGenerator> uniqueTicketIdGeneratorsForService) {
        this.uniqueTicketIdGeneratorsForService = uniqueTicketIdGeneratorsForService;
    }

    public void setServicesManager(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

}
