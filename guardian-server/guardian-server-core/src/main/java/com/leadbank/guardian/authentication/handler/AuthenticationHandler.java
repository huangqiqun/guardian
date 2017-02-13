package com.leadbank.guardian.authentication.handler;

import com.leadbank.guardian.authentication.principal.Credentials;

public interface AuthenticationHandler {

    boolean authenticate(Credentials credentials)
        throws AuthenticationException;

    boolean supports(Credentials credentials);
}
