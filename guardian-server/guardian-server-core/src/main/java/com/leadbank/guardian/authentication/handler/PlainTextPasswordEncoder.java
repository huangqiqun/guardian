package com.leadbank.guardian.authentication.handler;

public final class PlainTextPasswordEncoder implements PasswordEncoder {

    public String encode(final String password) {
        return password;
    }
}
