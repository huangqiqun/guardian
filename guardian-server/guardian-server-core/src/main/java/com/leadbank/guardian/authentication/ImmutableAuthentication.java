package com.leadbank.guardian.authentication;

import com.leadbank.guardian.authentication.principal.Principal;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class ImmutableAuthentication extends AbstractAuthentication {

    private static final long serialVersionUID = 3906647483978365235L;

    private static final Map<String, Object> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<String, Object>());

    final Date authenticatedDate;

    public ImmutableAuthentication(final Principal principal,
        final Map<String, Object> attributes) {
        super(principal, attributes == null || attributes.isEmpty()
            ? EMPTY_MAP : Collections.unmodifiableMap(attributes));

        this.authenticatedDate = new Date();
    }

    public ImmutableAuthentication(final Principal principal) {
        this(principal, null);
    }

    public Date getAuthenticatedDate() {
        return new Date(this.authenticatedDate.getTime());
    }
}
