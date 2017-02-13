package com.leadbank.guardian.client.authentication;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class DefaultAuthenticationRedirectStrategy implements AuthenticationRedirectStrategy {

    public void redirect(final HttpServletRequest request, final HttpServletResponse response,
            final String potentialRedirectUrl) throws IOException {
        response.sendRedirect(potentialRedirectUrl);
    }
}
