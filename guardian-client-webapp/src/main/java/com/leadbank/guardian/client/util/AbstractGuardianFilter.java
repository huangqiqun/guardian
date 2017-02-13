package com.leadbank.guardian.client.util;

import com.leadbank.guardian.client.validation.TicketValidationException;
import com.leadbank.guardian.facade.bean.AuthenticationRequest;
import com.leadbank.guardian.facade.bean.AuthenticationResponse;
import com.leadbank.guardian.facade.validate.AuthenticationFacade;
import com.leadbank.guardian.util.SpringContextUtil;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractGuardianFilter extends AbstractConfigurationFilter {
    
    public static final String CONST_GUARDIAN_ASSERTION = "_const_guardian_assertion_";

    private String artifactParameterName = "ticket";

    private String serviceParameterName = "service";
    
    private boolean encodeServiceUrl = true;

    private String serverName;

    private String service;

    public final void init(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            setServerName(getPropertyFromInitParams(filterConfig, "serverName", null));
            logger.trace("Loading serverName property: {}", this.serverName);
            setService(getPropertyFromInitParams(filterConfig, "service", null));
            logger.trace("Loading service property: {}", this.service);
            setArtifactParameterName(getPropertyFromInitParams(filterConfig, "artifactParameterName", "ticket"));
            logger.trace("Loading artifact parameter name property: {}", this.artifactParameterName);
            setServiceParameterName(getPropertyFromInitParams(filterConfig, "serviceParameterName", "service"));
            logger.trace("Loading serviceParameterName property: {} ", this.serviceParameterName);
            setEncodeServiceUrl(parseBoolean(getPropertyFromInitParams(filterConfig, "encodeServiceUrl", "true")));
            logger.trace("Loading encodeServiceUrl property: {}", this.encodeServiceUrl);
            
            initInternal(filterConfig);
        }
        init();
    }


    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        // template method
    }

    public void init() {
        CommonUtils.assertNotNull(this.artifactParameterName, "artifactParameterName cannot be null.");
        CommonUtils.assertNotNull(this.serviceParameterName, "serviceParameterName cannot be null.");
        CommonUtils.assertTrue(CommonUtils.isNotEmpty(this.serverName) || CommonUtils.isNotEmpty(this.service),
                "serverName or service must be set.");
        CommonUtils.assertTrue(CommonUtils.isBlank(this.serverName) || CommonUtils.isBlank(this.service),
                "serverName and service cannot both be set.  You MUST ONLY set one.");
    }

    public void destroy() {
        // nothing to do
    }

    protected final String constructServiceUrl(final HttpServletRequest request, final HttpServletResponse response) {
        return CommonUtils.constructServiceUrl(request, response, this.serverName);
    }

    public final void setServerName(final String serverName) {
        if (serverName != null && serverName.endsWith("/")) {
            this.serverName = serverName.substring(0, serverName.length() - 1);
            logger.info("Eliminated extra slash from serverName [{}].  It is now [{}]", serverName, this.serverName);
        } else {
            this.serverName = serverName;
        }
    }

    public final void setService(final String service) {
        this.service = service;
    }

    public final void setArtifactParameterName(final String artifactParameterName) {
        this.artifactParameterName = artifactParameterName;
    }

    public final void setServiceParameterName(final String serviceParameterName) {
        this.serviceParameterName = serviceParameterName;
    }

    public final void setEncodeServiceUrl(final boolean encodeServiceUrl) {
        this.encodeServiceUrl = encodeServiceUrl;
    }

    public final String getArtifactParameterName() {
        return this.artifactParameterName;
    }

    public final String getServiceParameterName() {
        return this.serviceParameterName;
    }
    
    protected String retrieveTicketFromRequest(final HttpServletRequest request) {
        return CommonUtils.safeGetParameter(request, getArtifactParameterName());
    }

    protected String retrieveCookieValue(final HttpServletRequest request, final String cookieName) {
        final Cookie cookie = org.springframework.web.util.WebUtils.getCookie(
                request, cookieName);

        return cookie == null ? null : cookie.getValue();
    }

    protected String authenticateFromDubbo(String tgc, String service, String sessionName) throws TicketValidationException {
        AuthenticationRequest request = new AuthenticationRequest();

        request.setTgc(tgc);
        request.setService(service);
        request.setSessionName(sessionName);
        try {
            logger.debug("Retrieving response from server.");
            AuthenticationFacade authenticationFacade = (AuthenticationFacade) SpringContextUtil.getBean("authenticationFacade");
            AuthenticationResponse response = authenticationFacade.authenticate(request);

            if (response == null) {
                throw new TicketValidationException("The Guardian server returned no response.");
            }

            logger.debug("Server response: {}", response);

            return response.getTicket();
        } catch (Exception e) {
            throw new TicketValidationException(e);
        }
    }

}
