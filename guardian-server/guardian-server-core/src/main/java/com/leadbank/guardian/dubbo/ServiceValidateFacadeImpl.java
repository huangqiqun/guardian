package com.leadbank.guardian.dubbo;

import com.leadbank.guardian.CentralAuthenticationService;
import com.leadbank.guardian.authentication.Authentication;
import com.leadbank.guardian.authentication.principal.Principal;
import com.leadbank.guardian.authentication.principal.WebApplicationService;
import com.leadbank.guardian.facade.bean.AssertionRequest;
import com.leadbank.guardian.facade.bean.AssertionResponse;
import com.leadbank.guardian.facade.validate.ServiceValidateFacade;
import com.leadbank.guardian.services.UnauthorizedServiceException;
import com.leadbank.guardian.services.support.ArgumentExtractor;
import com.leadbank.guardian.ticket.TicketException;
import com.leadbank.guardian.ticket.TicketValidationException;
import com.leadbank.guardian.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceValidateFacadeImpl implements ServiceValidateFacade {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private CentralAuthenticationService centralAuthenticationService;

    private ArgumentExtractor argumentExtractor;

    public AssertionResponse validate(AssertionRequest request) throws Exception {
        final WebApplicationService service = this.argumentExtractor.extractService(request);
        final String serviceTicketId = service != null ? service.getArtifactId() : null;

        if (service == null || serviceTicketId == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Could not process request; Service: %s, Service Ticket Id: %s", service, serviceTicketId));
            }
            //
            return generateErrorView("INVALID_REQUEST", "INVALID_REQUEST", null);
        }

        try {
            final Assertion assertion = this.centralAuthenticationService.validateServiceTicket(serviceTicketId, service);

            onSuccessfulValidation(serviceTicketId, assertion);

            final int authenticationChainSize = assertion.getChainedAuthentications().size();
            final Authentication authentication = assertion.getChainedAuthentications().get(authenticationChainSize - 1);
            final Principal principal = authentication.getPrincipal();
            final String principalId = principal.getId();

            AssertionResponse response = new AssertionResponse();
            response.setUser(principalId);
            response.setAttribute(principal.getAttributes());

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Successfully validated service ticket: %s", serviceTicketId));
            }

            return response;
        } catch (final TicketValidationException e) {
            return generateErrorView(e.getCode(), e.getCode(), new Object[]{serviceTicketId, e.getOriginalService().getId(), service.getId()});
        } catch (final TicketException te) {
            return generateErrorView(te.getCode(), te.getCode(), new Object[]{serviceTicketId});
        } catch (final UnauthorizedServiceException e) {
            return generateErrorView(e.getMessage(), e.getMessage(), null);
        }
    }

    protected void onSuccessfulValidation(final String serviceTicketId, final Assertion assertion) {
        // template method with nothing to do.
    }

    private AssertionResponse generateErrorView(final String code, final String description, final Object[] args) {
        AssertionResponse response = new AssertionResponse();
        response.setCode(code);
        response.setDescription(String.format(description, args));
        return response;
    }

    public void setCentralAuthenticationService(CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setArgumentExtractor(ArgumentExtractor argumentExtractor) {
        this.argumentExtractor = argumentExtractor;
    }

}
