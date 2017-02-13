package com.leadbank.guardian.authentication.principal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

public class SimplePrincipal implements Principal {

    private static final Map<String, Object> EMPTY_MAP = Collections
        .unmodifiableMap(new HashMap<String, Object>());

    private static final long serialVersionUID = -5265620187476296219L;

    private final String id;

    private Map<String, Object> attributes;

    public SimplePrincipal(final String id) {
        this(id, null);
    }

    public SimplePrincipal(final String id, final Map<String, Object> attributes) {
        Assert.notNull(id, "id cannot be null");
        this.id = id;

        this.attributes = attributes == null || attributes.isEmpty()
            ? EMPTY_MAP : Collections.unmodifiableMap(attributes);
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public String toString() {
        return this.id;
    }

    public int hashCode() {
        return super.hashCode() ^ this.id.hashCode();
    }

    public final String getId() {
        return this.id;
    }

    public boolean equals(final Object o) {
        if (o == null || !this.getClass().equals(o.getClass())) {
            return false;
        }

        final SimplePrincipal p = (SimplePrincipal) o;

        return this.id.equals(p.getId());
    }
}
