package com.leadbank.guardian.ticket.support;

public class NoOpLockingStrategy implements LockingStrategy {

    public boolean acquire() {
        return true;
    }

    public void release() {
        // Nothing to release
    }

}
