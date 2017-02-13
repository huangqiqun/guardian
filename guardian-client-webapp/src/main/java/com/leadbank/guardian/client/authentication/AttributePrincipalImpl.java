package com.leadbank.guardian.client.authentication;

import java.util.Collections;
import java.util.Map;

import com.leadbank.guardian.client.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributePrincipalImpl extends SimplePrincipal implements AttributePrincipal {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttributePrincipalImpl.class);

    private final Map<String, Object> attributes;

    public AttributePrincipalImpl(final String name) {
        this(name, Collections.<String, Object> emptyMap());
    }

    public AttributePrincipalImpl(final String name, final Map<String, Object> attributes) {
        super(name);
        this.attributes = attributes;

        CommonUtils.assertNotNull(this.attributes, "attributes cannot be null.");
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

}
