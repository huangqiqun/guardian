package com.leadbank.guardian.client.validation;

import com.leadbank.guardian.client.authentication.AttributePrincipal;
import com.leadbank.guardian.client.authentication.AttributePrincipalImpl;
import com.leadbank.guardian.client.util.CommonUtils;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

public final class AssertionImpl implements Assertion {

    private final Date validFromDate;

    private final Date validUntilDate;

    private final Date authenticationDate;

    private final Map<String, Object> attributes;

    private final AttributePrincipal principal;

    public AssertionImpl(final String name) {
        this(new AttributePrincipalImpl(name));
    }

    public AssertionImpl(final AttributePrincipal principal) {
        this(principal, Collections.<String, Object> emptyMap());
    }

    public AssertionImpl(final AttributePrincipal principal, final Map<String, Object> attributes) {
        this(principal, new Date(), null, new Date(), attributes);
    }

    public AssertionImpl(final AttributePrincipal principal, final Date validFromDate, final Date validUntilDate,
            final Date authenticationDate, final Map<String, Object> attributes) {
        this.principal = principal;
        this.validFromDate = validFromDate;
        this.validUntilDate = validUntilDate;
        this.attributes = attributes;
        this.authenticationDate = authenticationDate;

        CommonUtils.assertNotNull(this.principal, "principal cannot be null.");
        CommonUtils.assertNotNull(this.validFromDate, "validFromDate cannot be null.");
        CommonUtils.assertNotNull(this.attributes, "attributes cannot be null.");
    }

    public Date getAuthenticationDate() {
        return this.authenticationDate;
    }

    public Date getValidFromDate() {
        return this.validFromDate;
    }

    public Date getValidUntilDate() {
        return this.validUntilDate;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public AttributePrincipal getPrincipal() {
        return this.principal;
    }

    public boolean isValid() {
        if (this.validFromDate == null) {
            return true;
        }

        final Date now = new Date();
        return this.validFromDate.before(now) && (this.validUntilDate == null || this.validUntilDate.after(now));
    }
}
