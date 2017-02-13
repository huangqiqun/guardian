package com.leadbank.guardian.authentication;

import com.leadbank.guardian.authentication.principal.Principal;

import java.util.Date;
import java.util.HashMap;

public final class MutableAuthentication extends AbstractAuthentication {

    private static final long serialVersionUID = -4415875344376642246L;

    private final Date authenticatedDate;

    public MutableAuthentication(final Principal principal) {
        this(principal, new Date());
    }
    
    public MutableAuthentication(final Principal principal, final Date date) {
        super(principal, new HashMap<String, Object>());
        this.authenticatedDate = date;
    }

    public Date getAuthenticatedDate() {
        return this.authenticatedDate;
    }
}
