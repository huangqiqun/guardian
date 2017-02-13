package com.leadbank.guardian.authentication.handler;

public interface PrincipalNameTransformer {

    public String transform(String formUserId);
}

