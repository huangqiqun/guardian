package com.leadbank.guardian.services.support;

import com.leadbank.guardian.authentication.principal.WebApplicationService;
import com.leadbank.guardian.facade.bean.AbstractRequest;

import javax.servlet.http.HttpServletRequest;

public interface ArgumentExtractor {
    WebApplicationService extractService(HttpServletRequest request);

    WebApplicationService extractService(AbstractRequest request);
}
