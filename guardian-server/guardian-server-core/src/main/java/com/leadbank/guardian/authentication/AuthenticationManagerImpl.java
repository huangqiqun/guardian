package com.leadbank.guardian.authentication;

import com.leadbank.guardian.authentication.handler.AuthenticationException;
import com.leadbank.guardian.authentication.handler.AuthenticationHandler;
import com.leadbank.guardian.authentication.handler.BadCredentialsAuthenticationException;
import com.leadbank.guardian.authentication.handler.UnsupportedCredentialsException;
import com.leadbank.guardian.authentication.principal.Credentials;
import com.leadbank.guardian.authentication.principal.CredentialsToPrincipalResolver;
import com.leadbank.guardian.authentication.principal.Principal;

import java.util.List;

public final class AuthenticationManagerImpl extends AbstractAuthenticationManager {

    private List<AuthenticationHandler> authenticationHandlers;

    private List<CredentialsToPrincipalResolver> credentialsToPrincipalResolvers;

    @Override
    protected Pair<AuthenticationHandler, Principal> authenticateAndObtainPrincipal(final Credentials credentials) throws AuthenticationException {
        boolean foundSupported = false;
        boolean authenticated = false;
        AuthenticationHandler authenticatedClass = null;
        String handlerName;
        
        AuthenticationException unAuthSupportedHandlerException = BadCredentialsAuthenticationException.ERROR;
     
        for (final AuthenticationHandler authenticationHandler : this.authenticationHandlers) {
            if (authenticationHandler.supports(credentials)) {
                foundSupported = true;
                handlerName = authenticationHandler.getClass().getName();
                try {
                    if (!authenticationHandler.authenticate(credentials)) {
                        log.info("{} failed to authenticate {}", handlerName, credentials);
                    } else {
                        log.info("{} successfully authenticated {}", handlerName, credentials);
                        authenticatedClass = authenticationHandler;
                        authenticated = true;
                        break;
                    }
                } catch (AuthenticationException e) {
                    unAuthSupportedHandlerException = e;
                    logAuthenticationHandlerError(handlerName, credentials, e);
                } catch (Exception e) {
                    logAuthenticationHandlerError(handlerName, credentials, e);
                }
            }
        }

        if (!authenticated) {
            if (foundSupported) {
                throw unAuthSupportedHandlerException;
            }

            throw UnsupportedCredentialsException.ERROR;
        }

        foundSupported = false;

        for (final CredentialsToPrincipalResolver credentialsToPrincipalResolver : this.credentialsToPrincipalResolvers) {
            if (credentialsToPrincipalResolver.supports(credentials)) {
                final Principal principal = credentialsToPrincipalResolver.resolvePrincipal(credentials);
                log.info("Resolved principal " + principal);
                foundSupported = true;
                if (principal != null) {
                    return new Pair<AuthenticationHandler,Principal>(authenticatedClass, principal);
                }
            }
        }

        if (foundSupported) {
            if (log.isDebugEnabled()) {
                log.debug("CredentialsToPrincipalResolver found but no principal returned.");
            }

            throw BadCredentialsAuthenticationException.ERROR;
        }

        log.error("CredentialsToPrincipalResolver not found for " + credentials.getClass().getName());
        throw UnsupportedCredentialsException.ERROR;
    }

    public void setAuthenticationHandlers(
        final List<AuthenticationHandler> authenticationHandlers) {
        this.authenticationHandlers = authenticationHandlers;
    }

    public void setCredentialsToPrincipalResolvers(
        final List<CredentialsToPrincipalResolver> credentialsToPrincipalResolvers) {
        this.credentialsToPrincipalResolvers = credentialsToPrincipalResolvers;
    }
}
