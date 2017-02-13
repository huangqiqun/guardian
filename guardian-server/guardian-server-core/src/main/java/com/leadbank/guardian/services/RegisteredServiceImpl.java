package com.leadbank.guardian.services;

import com.leadbank.guardian.authentication.principal.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class RegisteredServiceImpl extends AbstractRegisteredService {

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    public void setServiceId(final String id) {
        this.serviceId = id;
    }

    public boolean matches(final Service service) {
        return service != null && PATH_MATCHER.match(serviceId.toLowerCase(), service.getId().toLowerCase());
    }

    protected AbstractRegisteredService newInstance() {
        return new RegisteredServiceImpl();
    }
}

