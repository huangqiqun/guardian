package com.leadbank.guardian.authentication.handler;

public interface PasswordEncoder {

    String encode(String password);
}
