package com.leadbank.guardian.authentication.principal;

public interface CredentialsToPrincipalResolver {

    Principal resolvePrincipal(Credentials credentials);

    boolean supports(Credentials credentials);
}
