package com.leadbank.guardian.authentication.handler;

public final class NoOpPrincipalNameTransformer implements PrincipalNameTransformer {

    public String transform(final String formUserId) {
        return formUserId;
    }
}
