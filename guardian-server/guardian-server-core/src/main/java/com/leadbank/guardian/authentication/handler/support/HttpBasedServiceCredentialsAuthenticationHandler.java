package com.leadbank.guardian.authentication.handler.support;

import com.leadbank.guardian.authentication.handler.AuthenticationHandler;
import com.leadbank.guardian.authentication.principal.Credentials;
import com.leadbank.guardian.authentication.principal.HttpBasedServiceCredentials;
import com.leadbank.guardian.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpBasedServiceCredentialsAuthenticationHandler implements AuthenticationHandler {

    private static final String PROTOCOL_HTTPS = "https";

    private boolean requireSecure = true;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private HttpClient httpClient;

    public boolean authenticate(final Credentials credentials) {
        final HttpBasedServiceCredentials serviceCredentials = (HttpBasedServiceCredentials) credentials;
        if (this.requireSecure
            && !serviceCredentials.getCallbackUrl().getProtocol().equals(
                PROTOCOL_HTTPS)) {
            if (log.isDebugEnabled()) {
                log.debug("Authentication failed because url was not secure.");
            }
            return false;
        }
        log
            .debug("Attempting to resolve credentials for "
                + serviceCredentials);

        return this.httpClient.isValidEndPoint(serviceCredentials
            .getCallbackUrl());
    }

    public boolean supports(final Credentials credentials) {
        return credentials != null
            && HttpBasedServiceCredentials.class.isAssignableFrom(credentials
                .getClass());
    }

    public void setHttpClient(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setRequireSecure(final boolean requireSecure) {
        this.requireSecure = requireSecure;
    }
}
