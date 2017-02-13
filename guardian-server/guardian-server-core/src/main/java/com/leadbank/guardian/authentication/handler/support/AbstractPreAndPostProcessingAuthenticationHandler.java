package com.leadbank.guardian.authentication.handler.support;

import com.leadbank.guardian.authentication.handler.AuthenticationException;
import com.leadbank.guardian.authentication.handler.NamedAuthenticationHandler;
import com.leadbank.guardian.authentication.principal.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPreAndPostProcessingAuthenticationHandler
    implements NamedAuthenticationHandler {
    
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    
    private String name = getClass().getName();

    protected boolean preAuthenticate(final Credentials credentials) {
        return true;
    }

    protected boolean postAuthenticate(final Credentials credentials,
        final boolean authenticated) {
        return authenticated;
    }
    
    public final void setName(final String name) {
        this.name = name;
    }
    
    public final String getName() {
        return this.name;
    }

    public final boolean authenticate(final Credentials credentials)
        throws AuthenticationException {

        if (!preAuthenticate(credentials)) {
            return false;
        }

        final boolean authenticated = doAuthentication(credentials);

        return postAuthenticate(credentials, authenticated);
    }

    protected abstract boolean doAuthentication(final Credentials credentials)
        throws AuthenticationException;
}
