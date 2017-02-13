package com.leadbank.guardian.dubbo;

import com.leadbank.guardian.CentralAuthenticationService;
import com.leadbank.guardian.authentication.principal.WebApplicationService;
import com.leadbank.guardian.facade.bean.AuthenticationRequest;
import com.leadbank.guardian.facade.bean.AuthenticationResponse;
import com.leadbank.guardian.facade.validate.AuthenticationFacade;
import com.leadbank.guardian.services.support.ArgumentExtractor;
import com.leadbank.guardian.ticket.TicketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationFacadeImpl implements AuthenticationFacade {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private CentralAuthenticationService centralAuthenticationService;

    private ArgumentExtractor argumentExtractor;

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        //获取TGC
        final String ticketGrantingTicketValueFromDubbo = request.getTgc();
        //Service
        final WebApplicationService service = this.argumentExtractor.extractService(request);
        if (service != null && logger.isDebugEnabled()) {
            logger.debug("Get service in Request: " + service.getId());
        }
        final String sessionName = request.getSessionName();
        //已登录，则用TGT签发ST
        String serviceTicketId = null;
        if (ticketGrantingTicketValueFromDubbo != null && service != null) {
            try {
                serviceTicketId = this.centralAuthenticationService.grantServiceTicket(ticketGrantingTicketValueFromDubbo, service, sessionName);
            } catch (final TicketException e) {
                this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketValueFromDubbo);
                if (logger.isDebugEnabled()) {
                    logger.debug("Attempted to generate a ServiceTicket using renew=true with different credentials", e);
                }
            }
        }

        AuthenticationResponse res = new AuthenticationResponse();
        res.setTicket(serviceTicketId);
        return res;
    }

    public void setCentralAuthenticationService(CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setArgumentExtractor(ArgumentExtractor argumentExtractor) {
        this.argumentExtractor = argumentExtractor;
    }
}
