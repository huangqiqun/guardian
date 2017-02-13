package com.leadbank.guardian.services.support;

import com.leadbank.guardian.authentication.principal.WebApplicationService;
import com.leadbank.guardian.facade.bean.AbstractRequest;
import com.leadbank.guardian.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractSingleSignOutEnabledArgumentExtractor implements
    ArgumentExtractor {
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    private boolean disableSingleSignOut = false;
    
    private HttpClient httpClient;
    
    public void setHttpClient(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    protected HttpClient getHttpClientIfSingleSignOutEnabled() {
        return !this.disableSingleSignOut ? this.httpClient : null; 
    }
    
    public void setDisableSingleSignOut(final boolean disableSingleSignOut) {
        this.disableSingleSignOut = disableSingleSignOut;
    }
    
    public final WebApplicationService extractService(final HttpServletRequest request) {
        final WebApplicationService service = extractServiceInternal(request);

        if (service == null) {
            log.debug("Extractor did not generate service.");
        } else {
            log.debug("Extractor generated service for: " + service.getId());
        }

        return service;
    }

    public final WebApplicationService extractService(final AbstractRequest request) {
        final WebApplicationService service = extractServiceInternal(request);

        if (service == null) {
            log.debug("Extractor did not generate service.");
        } else {
            log.debug("Extractor generated service for: " + service.getId());
        }

        return service;
    }
    
    protected abstract WebApplicationService extractServiceInternal(final HttpServletRequest request);

    protected abstract WebApplicationService extractServiceInternal(final AbstractRequest request);
}
