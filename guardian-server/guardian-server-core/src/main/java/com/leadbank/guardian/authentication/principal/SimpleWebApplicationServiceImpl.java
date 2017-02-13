package com.leadbank.guardian.authentication.principal;

import com.leadbank.guardian.authentication.principal.Response.ResponseType;
import com.leadbank.guardian.facade.bean.AbstractRequest;
import com.leadbank.guardian.util.HttpClient;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public final class SimpleWebApplicationServiceImpl extends
    AbstractWebApplicationService {

    private static final String CONST_PARAM_SERVICE = "service";

    private static final String CONST_PARAM_TARGET_SERVICE = "targetService";

    private static final String CONST_PARAM_TICKET = "ticket";

    private static final String CONST_PARAM_METHOD = "method";
    
    private final ResponseType responseType;

    private static final long serialVersionUID = 8334068957483758042L;

    public SimpleWebApplicationServiceImpl(final String id) {
        this(id, null, null, null, null);
    }

    public SimpleWebApplicationServiceImpl(final String id, final HttpClient httpClient) {
        this(id, null, null, null, httpClient);
    }

    private SimpleWebApplicationServiceImpl(final String id,
        final String originalUrl, final String artifactId,
        final ResponseType responseType, final HttpClient httpClient) {
        super(id, originalUrl, artifactId, httpClient);
        this.responseType = responseType;
    }
    
    public static SimpleWebApplicationServiceImpl createServiceFrom(final HttpServletRequest request) {
        return createServiceFrom(request, null);
    }

    public static SimpleWebApplicationServiceImpl createServiceFrom(
        final HttpServletRequest request, final HttpClient httpClient) {
        final String targetService = request.getParameter(CONST_PARAM_TARGET_SERVICE);
        final String method = request.getParameter(CONST_PARAM_METHOD);
        final String serviceToUse = StringUtils.hasText(targetService) ? targetService : request.getParameter(CONST_PARAM_SERVICE);

        if (!StringUtils.hasText(serviceToUse)) {
            return null;
        }

        final String id = cleanupUrl(serviceToUse);
        final String artifactId = request.getParameter(CONST_PARAM_TICKET);

        return new SimpleWebApplicationServiceImpl(id, serviceToUse,
            artifactId, "POST".equals(method) ? ResponseType.POST
                : ResponseType.REDIRECT, httpClient);
    }

    public static SimpleWebApplicationServiceImpl createServiceFrom(final AbstractRequest request, final HttpClient httpClient) {
        final String serviceToUse = request.getService();
        
        if (!StringUtils.hasText(serviceToUse)) {
            return null;
        }

        final String id = cleanupUrl(serviceToUse);
        final String artifactId = request.getTicket();

        return new SimpleWebApplicationServiceImpl(id, serviceToUse, artifactId, ResponseType.POST, httpClient);
    }

    public Response getResponse(final String ticketId) {
        final Map<String, String> parameters = new HashMap<String, String>();

        if (StringUtils.hasText(ticketId)) {
            parameters.put(CONST_PARAM_TICKET, ticketId);
        }

        if (ResponseType.POST == this.responseType) {
            return Response.getPostResponse(getOriginalUrl(), parameters);
        }
        return Response.getRedirectResponse(getOriginalUrl(), parameters);
    }
}
