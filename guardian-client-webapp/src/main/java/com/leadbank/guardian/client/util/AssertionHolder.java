package com.leadbank.guardian.client.util;

import com.leadbank.guardian.client.validation.Assertion;

public class AssertionHolder {

    private static final ThreadLocal<Assertion> threadLocal = new ThreadLocal<Assertion>();

    public static Assertion getAssertion() {
        return threadLocal.get();
    }

    public static void setAssertion(final Assertion assertion) {
        threadLocal.set(assertion);
    }

    public static void clear() {
        threadLocal.set(null);
    }
}
