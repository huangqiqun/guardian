package com.leadbank.guardian.client.authentication;

import com.leadbank.guardian.client.util.CommonUtils;

import java.io.Serializable;
import java.security.Principal;

public class SimplePrincipal implements Principal, Serializable {

    private final String name;

    public SimplePrincipal(final String name) {
        this.name = name;
        CommonUtils.assertNotNull(this.name, "name cannot be null.");
    }

    public final String getName() {
        return this.name;
    }

    public String toString() {
        return getName();
    }

    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        } else if (!(o instanceof SimplePrincipal)) {
            return false;
        } else {
            return getName().equals(((SimplePrincipal) o).getName());
        }
    }

    public int hashCode() {
        return 37 * getName().hashCode();
    }
}
