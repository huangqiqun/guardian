package com.leadbank.guardian.services.support;

import com.leadbank.guardian.authentication.principal.SimpleWebApplicationServiceImpl;
import com.leadbank.guardian.authentication.principal.WebApplicationService;
import com.leadbank.guardian.facade.bean.AbstractRequest;

import javax.servlet.http.HttpServletRequest;

public final class GuardianArgumentExtractor extends AbstractSingleSignOutEnabledArgumentExtractor {

    public final WebApplicationService extractServiceInternal(final HttpServletRequest request) {
        return SimpleWebApplicationServiceImpl.createServiceFrom(request, getHttpClientIfSingleSignOutEnabled());
    }

    public final WebApplicationService extractServiceInternal(final AbstractRequest request) {
        return SimpleWebApplicationServiceImpl.createServiceFrom(request, getHttpClientIfSingleSignOutEnabled());
    }
}
