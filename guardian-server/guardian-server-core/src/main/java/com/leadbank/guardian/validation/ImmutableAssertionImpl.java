package com.leadbank.guardian.validation;

import java.util.Collections;
import java.util.List;

import com.leadbank.guardian.authentication.Authentication;
import com.leadbank.guardian.authentication.principal.Service;
import org.springframework.util.Assert;

public final class ImmutableAssertionImpl implements Assertion {

    private final List<Authentication> principals;

    private final boolean fromNewLogin;

    private final Service service;

    public ImmutableAssertionImpl(final List<Authentication> principals, final Service service,
        final boolean fromNewLogin) {
        Assert.notNull(principals, "principals cannot be null");
        Assert.notNull(service, "service cannot be null");
        Assert.notEmpty(principals, "principals cannot be empty");

        this.principals = principals;
        this.service = service;
        this.fromNewLogin = fromNewLogin;
    }

    public List<Authentication> getChainedAuthentications() {
        return Collections.unmodifiableList(this.principals);
    }

    public boolean isFromNewLogin() {
        return this.fromNewLogin;
    }

    public Service getService() {
        return this.service;
    }

    public boolean equals(final Object o) {
        if (o == null
            || !this.getClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        
        final Assertion a = (Assertion) o;
        
        return this.service.equals(a.getService()) && this.fromNewLogin == a.isFromNewLogin() && this.principals.equals(a.getChainedAuthentications());
    }

    public int hashCode() {
        return 15 * this.service.hashCode() ^ this.principals.hashCode();
    }

    public String toString() {
        return "[principals={" + this.principals.toString() + "} for service=" + this.service.toString() + "]";
    }
}
