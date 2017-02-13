package com.leadbank.guardian.authentication;

import com.leadbank.guardian.authentication.handler.AuthenticationException;
import com.leadbank.guardian.authentication.handler.AuthenticationHandler;
import com.leadbank.guardian.authentication.handler.NamedAuthenticationHandler;
import com.leadbank.guardian.authentication.principal.Credentials;
import com.leadbank.guardian.authentication.principal.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAuthenticationManager implements AuthenticationManager {

    protected final Logger log = LoggerFactory.getLogger(AuthenticationManagerImpl.class);

    private List<AuthenticationMetaDataPopulator> authenticationMetaDataPopulators = new ArrayList<AuthenticationMetaDataPopulator>();

    public final Authentication authenticate(final Credentials credentials) throws AuthenticationException {

        final Pair<AuthenticationHandler, Principal> pair = authenticateAndObtainPrincipal(credentials);

        // we can only get here if the above method doesn't throw an exception. And if it doesn't, then the pair must not be null.
        final Principal p = pair.getSecond();
        log.info(String.format("Principal found: %s", p.getId()));

        if (log.isDebugEnabled()) {
            log.debug(String.format("Attribute map for %s: %s", p.getId(), p.getAttributes()));
        }

        Authentication authentication = new MutableAuthentication(p);

        if (pair.getFirst()instanceof NamedAuthenticationHandler) {
            final NamedAuthenticationHandler a = (NamedAuthenticationHandler) pair.getFirst();
            authentication.getAttributes().put(AuthenticationManager.AUTHENTICATION_METHOD_ATTRIBUTE, a.getName());
        }

        for (final AuthenticationMetaDataPopulator authenticationMetaDataPopulator : this.authenticationMetaDataPopulators) {
            authentication = authenticationMetaDataPopulator
                .populateAttributes(authentication, credentials);
        }

        return new ImmutableAuthentication(authentication.getPrincipal(),
            authentication.getAttributes());
    }

    public final void setAuthenticationMetaDataPopulators(final List<AuthenticationMetaDataPopulator> authenticationMetaDataPopulators) {
        this.authenticationMetaDataPopulators = authenticationMetaDataPopulators;
    }

    protected abstract Pair<AuthenticationHandler,Principal> authenticateAndObtainPrincipal(Credentials credentials) throws AuthenticationException;


    protected void logAuthenticationHandlerError(final String handlerName, final Credentials credentials, final Exception e) {
        if (this.log.isErrorEnabled())
            this.log.error("{} threw error authenticating {}", new Object[] {handlerName, credentials, e});
    }


    protected static class Pair<A,B> {

        private final A first;

        private final B second;

        public Pair(final A first, final B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() {
            return this.first;
        }


        public B getSecond() {
            return this.second;
        }
    }

}
