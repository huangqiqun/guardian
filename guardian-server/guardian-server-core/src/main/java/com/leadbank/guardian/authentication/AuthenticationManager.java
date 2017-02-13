package com.leadbank.guardian.authentication;

import com.leadbank.guardian.authentication.handler.AuthenticationException;
import com.leadbank.guardian.authentication.principal.Credentials;

public interface AuthenticationManager {
    
    String AUTHENTICATION_METHOD_ATTRIBUTE = "authenticationMethod";

    Authentication authenticate(final Credentials credentials)
        throws AuthenticationException;
}
