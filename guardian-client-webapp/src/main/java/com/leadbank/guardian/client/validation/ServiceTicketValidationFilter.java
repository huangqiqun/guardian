package com.leadbank.guardian.client.validation;

import com.leadbank.guardian.client.ssl.HttpURLConnectionFactory;
import com.leadbank.guardian.client.ssl.HttpsURLConnectionFactory;
import com.leadbank.guardian.client.util.AbstractGuardianFilter;
import com.leadbank.guardian.client.util.CommonUtils;
import com.leadbank.guardian.client.util.ReflectUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceTicketValidationFilter extends AbstractGuardianFilter {

    private TicketValidator ticketValidator;

    private boolean redirectAfterValidation = true;

    private boolean exceptionOnValidationFailure = false;

    private boolean useSession = true;

    protected TicketValidator getTicketValidator(final FilterConfig filterConfig) {
        final String casServerUrlPrefix = getPropertyFromInitParams(filterConfig, "guardianServerUrlPrefix", null);
        final Guardian10ServiceTicketValidator validator = new Guardian10ServiceTicketValidator(casServerUrlPrefix);

        final HttpURLConnectionFactory factory = new HttpsURLConnectionFactory(getHostnameVerifier(filterConfig),
                getSSLConfig(filterConfig));
        validator.setURLConnectionFactory(factory);
        validator.setEncoding(getPropertyFromInitParams(filterConfig, "encoding", null));

        return validator;
    }

    protected Properties getSSLConfig(final FilterConfig filterConfig) {
        final Properties properties = new Properties();
        final String fileName = getPropertyFromInitParams(filterConfig, "sslConfigFile", null);

        if (fileName != null) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(fileName);
                properties.load(fis);
                logger.trace("Loaded {} entries from {}", properties.size(), fileName);
            } catch (final IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            } finally {
                CommonUtils.closeQuietly(fis);
            }
        }
        return properties;
    }

    protected HostnameVerifier getHostnameVerifier(final FilterConfig filterConfig) {
        final String className = getPropertyFromInitParams(filterConfig, "hostnameVerifier", null);
        logger.trace("Using hostnameVerifier parameter: {}", className);
        final String config = getPropertyFromInitParams(filterConfig, "hostnameVerifierConfig", null);
        logger.trace("Using hostnameVerifierConfig parameter: {}", config);
        if (className != null) {
            if (config != null) {
                return ReflectUtils.newInstance(className, config);
            } else {
                return ReflectUtils.newInstance(className);
            }
        }
        return null;
    }

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        setExceptionOnValidationFailure(parseBoolean(getPropertyFromInitParams(filterConfig,
                "exceptionOnValidationFailure", "false")));
        logger.trace("Setting exceptionOnValidationFailure parameter: {}", this.exceptionOnValidationFailure);
        setRedirectAfterValidation(parseBoolean(getPropertyFromInitParams(filterConfig, "redirectAfterValidation",
                "true")));
        logger.trace("Setting redirectAfterValidation parameter: {}", this.redirectAfterValidation);
        setUseSession(parseBoolean(getPropertyFromInitParams(filterConfig, "useSession", "true")));
        logger.trace("Setting useSession parameter: {}", this.useSession);

        if (!this.useSession && this.redirectAfterValidation) {
            logger.warn("redirectAfterValidation parameter may not be true when useSession parameter is false. Resetting it to false in order to prevent infinite redirects.");
            setRedirectAfterValidation(false);
        }

        setTicketValidator(getTicketValidator(filterConfig));
        super.initInternal(filterConfig);
    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.ticketValidator, "ticketValidator cannot be null.");
    }

    protected boolean preFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                                     final FilterChain filterChain) throws IOException, ServletException {
        return true;
    }

    protected void onSuccessfulValidation(final HttpServletRequest request, final HttpServletResponse response,
            final Assertion assertion) {
        // nothing to do here.
    }

    protected void onFailedValidation(final HttpServletRequest request, final HttpServletResponse response) {
        // nothing to do here.
    }

    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {

        if (!preFilter(servletRequest, servletResponse, filterChain)) {
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        String ticket = retrieveTicketFromRequest(request);
        ticket = CommonUtils.isNotBlank(ticket) ? ticket : (String) request.getAttribute("ticket");

        if (CommonUtils.isNotBlank(ticket)) {
            logger.debug("Attempting to validate ticket: {}", ticket);

            try {
                final Assertion assertion = this.ticketValidator.validateFromDubbo(ticket,
                        constructServiceUrl(request, response));

                logger.debug("Successfully authenticated user: {}", assertion.getPrincipal().getName());

                request.setAttribute(CONST_GUARDIAN_ASSERTION, assertion);

                if (this.useSession) {
                    request.getSession().setAttribute(CONST_GUARDIAN_ASSERTION, assertion);
                }
                onSuccessfulValidation(request, response, assertion);

                if (this.redirectAfterValidation) {
                    logger.debug("Redirecting after successful ticket validation.");
                    response.sendRedirect(constructServiceUrl(request, response));
                    return;
                }
            } catch (final TicketValidationException e) {
                logger.debug(e.getMessage(), e);

                onFailedValidation(request, response);

                if (this.exceptionOnValidationFailure) {
                    throw new ServletException(e);
                }

                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

                return;
            }
        }

        filterChain.doFilter(request, response);

    }

    public final void setTicketValidator(final TicketValidator ticketValidator) {
        this.ticketValidator = ticketValidator;
    }

    public final void setRedirectAfterValidation(final boolean redirectAfterValidation) {
        this.redirectAfterValidation = redirectAfterValidation;
    }

    public final void setExceptionOnValidationFailure(final boolean exceptionOnValidationFailure) {
        this.exceptionOnValidationFailure = exceptionOnValidationFailure;
    }

    public final void setUseSession(final boolean useSession) {
        this.useSession = useSession;
    }
}
