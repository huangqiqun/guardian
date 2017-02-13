package com.leadbank.guardian.client.util;

import com.leadbank.guardian.client.authentication.AttributePrincipal;
import com.leadbank.guardian.client.validation.Assertion;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

public final class HttpServletRequestWrapperFilter extends AbstractConfigurationFilter {

    private String roleAttribute;

    private boolean ignoreCase;

    public void destroy() {
        // nothing to do
    }

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        final AttributePrincipal principal = retrievePrincipalFromSessionOrRequest(servletRequest);

        filterChain.doFilter(new CasHttpServletRequestWrapper((HttpServletRequest) servletRequest, principal),
                servletResponse);
    }

    protected AttributePrincipal retrievePrincipalFromSessionOrRequest(final ServletRequest servletRequest) {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpSession session = request.getSession(false);
        final Assertion assertion = (Assertion) (session == null ? request
                .getAttribute(AbstractGuardianFilter.CONST_GUARDIAN_ASSERTION) : session
                .getAttribute(AbstractGuardianFilter.CONST_GUARDIAN_ASSERTION));

        return assertion == null ? null : assertion.getPrincipal();
    }

    public void init(final FilterConfig filterConfig) throws ServletException {
        this.roleAttribute = getPropertyFromInitParams(filterConfig, "roleAttribute", null);
        this.ignoreCase = Boolean.parseBoolean(getPropertyFromInitParams(filterConfig, "ignoreCase", "false"));
    }

    final class CasHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final AttributePrincipal principal;

        CasHttpServletRequestWrapper(final HttpServletRequest request, final AttributePrincipal principal) {
            super(request);
            this.principal = principal;
        }

        public Principal getUserPrincipal() {
            return this.principal;
        }

        public String getRemoteUser() {
            return principal != null ? this.principal.getName() : null;
        }

        public boolean isUserInRole(final String role) {
            if (CommonUtils.isBlank(role)) {
                logger.debug("No valid role provided.  Returning false.");
                return false;
            }

            if (this.principal == null) {
                logger.debug("No Principal in Request.  Returning false.");
                return false;
            }

            if (CommonUtils.isBlank(roleAttribute)) {
                logger.debug("No Role Attribute Configured. Returning false.");
                return false;
            }

            final Object value = this.principal.getAttributes().get(roleAttribute);

            if (value instanceof Collection<?>) {
                for (final Object o : (Collection<?>) value) {
                    if (rolesEqual(role, o)) {
                        logger.debug("User [{}] is in role [{}]: true", getRemoteUser(), role);
                        return true;
                    }
                }
            }

            final boolean isMember = rolesEqual(role, value);
            logger.debug("User [{}] is in role [{}]: {}", getRemoteUser(), role, isMember);
            return isMember;
        }

        private boolean rolesEqual(final String given, final Object candidate) {
            return ignoreCase ? given.equalsIgnoreCase(candidate.toString()) : given.equals(candidate);
        }
    }
}
