package com.leadbank.guardian.web.support;

import com.leadbank.guardian.authentication.principal.WebApplicationService;
import com.leadbank.guardian.services.support.ArgumentExtractor;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public final class WebUtils {

    public static final String CAS_ACCESS_DENIED_REASON = "CAS_ACCESS_DENIED_REASON";

    public static WebApplicationService getService(
            final List<ArgumentExtractor> argumentExtractors,
            final HttpServletRequest request) {
        for (final ArgumentExtractor argumentExtractor : argumentExtractors) {
            final WebApplicationService service = argumentExtractor
                    .extractService(request);

            if (service != null) {
                return service;
            }
        }

        return null;
    }

    public static String getTicketGrantingTicketId(
            final HttpServletRequest request) {
        return request.getParameter("ticketGrantingTicketId");
    }

    public static String getServiceTicketFromRequest(
            final HttpServletRequest request) {
        return request.getParameter("serviceTicketId");
    }

    public static String getLoginTicketFromRequest(final HttpServletRequest request) {
        return request.getParameter("lt");
    }
}
