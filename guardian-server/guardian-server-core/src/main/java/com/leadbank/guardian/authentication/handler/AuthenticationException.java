package com.leadbank.guardian.authentication.handler;

public abstract class AuthenticationException extends Exception {

    private String code;

    private String type = "error";

    public AuthenticationException(final String code) {
        this.code = code;
    }

    public AuthenticationException(final String code, final String msg) {
        super(msg);
        this.code = code;
    }

    public AuthenticationException(final String code, final String msg, final String type) {
        super(msg);
        this.code = code;
        this.type = type;
    }

    public AuthenticationException(final String code, final Throwable throwable) {
        super(throwable);
        this.code = code;
    }

    public final String getType() {
        return this.type;
    }

    public final String getCode() {
        return this.code;
    }

    @Override
    public final String toString() {
        String msg = getCode();
        if (getMessage() != null && getMessage().trim().length() > 0)
            msg = ":" + getMessage();
        return msg;
    }

}
