package com.leadbank.guardian.authentication.handler;

public class BadCredentialsAuthenticationException extends
    AuthenticationException {

    public static final BadCredentialsAuthenticationException ERROR = new BadCredentialsAuthenticationException();

    public static final String CODE = "error.authentication.credentials.bad";

    public BadCredentialsAuthenticationException() {
        super(CODE);
    }

    public BadCredentialsAuthenticationException(final Throwable throwable) {
        super(CODE, throwable);
    }

    public BadCredentialsAuthenticationException(final String code) {
        super(code);
    }

    public BadCredentialsAuthenticationException(final String code,
        final Throwable throwable) {
        super(code, throwable);
    }
}
