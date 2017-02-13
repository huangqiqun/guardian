package com.leadbank.guardian.authentication.handler.support;

import com.leadbank.guardian.authentication.handler.*;
import com.leadbank.guardian.authentication.principal.Credentials;
import com.leadbank.guardian.authentication.principal.UsernamePasswordCredentials;

public abstract class AbstractUsernamePasswordAuthenticationHandler extends
    AbstractPreAndPostProcessingAuthenticationHandler {

    private static final Class<UsernamePasswordCredentials> DEFAULT_CLASS = UsernamePasswordCredentials.class;

    private Class< ? > classToSupport = DEFAULT_CLASS;

    private boolean supportSubClasses = true;

    private PasswordEncoder passwordEncoder = new PlainTextPasswordEncoder();

    private PrincipalNameTransformer principalNameTransformer = new NoOpPrincipalNameTransformer();

    protected final boolean doAuthentication(final Credentials credentials)
        throws AuthenticationException {
        return authenticateUsernamePasswordInternal((UsernamePasswordCredentials) credentials);
    }

    protected abstract boolean authenticateUsernamePasswordInternal(
        final UsernamePasswordCredentials credentials)
        throws AuthenticationException;

    protected final PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    protected final PrincipalNameTransformer getPrincipalNameTransformer() {
        return this.principalNameTransformer;
    }

    public final void setClassToSupport(final Class< ? > classToSupport) {
        this.classToSupport = classToSupport;
    }

    public final void setSupportSubClasses(final boolean supportSubClasses) {
        this.supportSubClasses = supportSubClasses;
    }

    public final void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public final void setPrincipalNameTransformer(final PrincipalNameTransformer principalNameTransformer) {
        this.principalNameTransformer = principalNameTransformer;
    }

    public final boolean supports(final Credentials credentials) {
        return credentials != null
            && (this.classToSupport.equals(credentials.getClass()) || (this.classToSupport
                .isAssignableFrom(credentials.getClass()))
                && this.supportSubClasses);
    }
}
