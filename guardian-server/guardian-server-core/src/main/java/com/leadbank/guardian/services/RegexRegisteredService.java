package com.leadbank.guardian.services;

import com.leadbank.guardian.authentication.principal.Service;

import java.util.regex.Pattern;

public class RegexRegisteredService extends AbstractRegisteredService {

    private transient Pattern servicePattern;
    
    public void setServiceId(final String id) {
        servicePattern = createPattern(id);
        serviceId = id;
    }

    public boolean matches(final Service service) {
        if (servicePattern == null) {
            servicePattern = createPattern(serviceId);
        }
        return service != null && servicePattern.matcher(service.getId()).matches();
    }

    protected AbstractRegisteredService newInstance() {
        return new RegexRegisteredService();
    }

    private Pattern createPattern(final String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null.");
        }
        return Pattern.compile(pattern);
    }
}
