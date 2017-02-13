package com.leadbank.guardian.client.authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.leadbank.guardian.client.util.AbstractGuardianFilter;
import com.leadbank.guardian.client.util.CommonUtils;
import com.leadbank.guardian.client.util.ReflectUtils;
import com.leadbank.guardian.client.validation.Assertion;
import com.leadbank.guardian.client.validation.TicketValidationException;
import com.leadbank.guardian.facade.validate.AuthenticationFacade;
import com.leadbank.guardian.util.SpringContextUtil;

public class AuthenticationFilter extends AbstractGuardianFilter {

    private String guardianClientLoginUrl;

    private AuthenticationRedirectStrategy authenticationRedirectStrategy = new DefaultAuthenticationRedirectStrategy();
    
    private UrlPatternMatcherStrategy ignoreUrlPatternMatcherStrategyClass = null;

    private static final Map<String, Class<? extends UrlPatternMatcherStrategy>> PATTERN_MATCHER_TYPES =
            new HashMap<String, Class<? extends UrlPatternMatcherStrategy>>();
    
    static {
        PATTERN_MATCHER_TYPES.put("CONTAINS", ContainsPatternUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES.put("REGEX", RegexUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES.put("EXACT", ExactUrlPatternMatcherStrategy.class);
    }
    
    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
            setGuardianClientLoginUrl(getPropertyFromInitParams(filterConfig, "guardianClientLoginUrl", null));

            final String ignorePattern = getPropertyFromInitParams(filterConfig, "ignorePattern", null);
            logger.trace("Loaded ignorePattern parameter: {}", ignorePattern);
            
            final String ignoreUrlPatternType = getPropertyFromInitParams(filterConfig, "ignoreUrlPatternType", "REGEX");
            logger.trace("Loaded ignoreUrlPatternType parameter: {}", ignoreUrlPatternType);
            
            if (ignorePattern != null) {
                final Class<? extends UrlPatternMatcherStrategy> ignoreUrlMatcherClass = PATTERN_MATCHER_TYPES.get(ignoreUrlPatternType);
                if (ignoreUrlMatcherClass != null) {
                    this.ignoreUrlPatternMatcherStrategyClass = ReflectUtils.newInstance(ignoreUrlMatcherClass.getName());
                } else {
                    try {
                        logger.trace("Assuming {} is a qualified class name...", ignoreUrlPatternType);
                        this.ignoreUrlPatternMatcherStrategyClass = ReflectUtils.newInstance(ignoreUrlPatternType);
                    } catch (final IllegalArgumentException e) {
                        logger.error("Could not instantiate class [{}]", ignoreUrlPatternType, e);
                    }
                }
                if (this.ignoreUrlPatternMatcherStrategyClass != null) {
                    this.ignoreUrlPatternMatcherStrategyClass.setPattern(ignorePattern);
                }
            }
            
            final String authenticationRedirectStrategyClass = getPropertyFromInitParams(filterConfig,
                    "authenticationRedirectStrategyClass", null);

            if (authenticationRedirectStrategyClass != null) {
                this.authenticationRedirectStrategy = ReflectUtils.newInstance(authenticationRedirectStrategyClass);
            }
        }
    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.guardianClientLoginUrl, "GuardianClientLoginUrl cannot be null.");
    }

    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                               final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        if (isRequestUrlExcluded(request)) {
            logger.debug("Request is ignored.");
            filterChain.doFilter(request, response);
            return;
        }
        
        final HttpSession session = request.getSession(false);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_GUARDIAN_ASSERTION) : null;
        
        if (assertion != null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String serviceUrl = constructServiceUrl(request, response);
        final String ticket = retrieveTicketFromRequest(request);

        if (CommonUtils.isNotBlank(ticket)) {
            filterChain.doFilter(request, response);
            return;
        }

        //TGC cookie
        final String ticketGrantingTicketId = retrieveCookieValue(request, "GuardianTGC");
        if (!CommonUtils.isBlank(ticketGrantingTicketId)) {
            try {
                final String sessionName = (String) request.getAttribute("sessionName");
                final String serviceTicket = authenticateFromDubbo(ticketGrantingTicketId, serviceUrl, sessionName);
                request.setAttribute("ticket", serviceTicket);

                filterChain.doFilter(request, response);
                return;
            } catch (TicketValidationException e) {
                logger.debug(e.getMessage(), e);
                return;
            }
        }

        logger.debug("no ticket and no assertion found");

        final String originalUrl = request.getRequestURI();
        final String urlToRedirectTo = CommonUtils.constructRedirectUrl(this.guardianClientLoginUrl, getServiceParameterName(), serviceUrl, originalUrl);
        logger.debug("redirecting to \"{}\"", urlToRedirectTo);

        this.authenticationRedirectStrategy.redirect(request, response, urlToRedirectTo);
    }

    public final void setGuardianClientLoginUrl(final String guardianClientLoginUrl) {
        this.guardianClientLoginUrl = guardianClientLoginUrl;
    }

    private boolean isRequestUrlExcluded(final HttpServletRequest request) {
        if (this.ignoreUrlPatternMatcherStrategyClass == null) {
            return false;
        }
        
        final StringBuffer urlBuffer = request.getRequestURL();
        if (request.getQueryString() != null) {
            urlBuffer.append("?").append(request.getQueryString());
        }
        final String requestUri = urlBuffer.toString();
        return this.ignoreUrlPatternMatcherStrategyClass.matches(requestUri);
    }
}
