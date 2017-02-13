package com.leadbank.guardian.authentication.handler;

public final class UnsupportedCredentialsException extends
    AuthenticationException {

    public static final UnsupportedCredentialsException ERROR = new UnsupportedCredentialsException();

    private static final String CODE = "error.authentication.credentials.unsupported";

    public UnsupportedCredentialsException() {
        super(CODE);
    }

    public UnsupportedCredentialsException(final Throwable throwable) {
        super(CODE, throwable);
    }
}
