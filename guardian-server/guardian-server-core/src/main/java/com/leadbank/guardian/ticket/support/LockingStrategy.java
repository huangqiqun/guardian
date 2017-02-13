package com.leadbank.guardian.ticket.support;

public interface LockingStrategy {

    boolean acquire();

    void release();
}
