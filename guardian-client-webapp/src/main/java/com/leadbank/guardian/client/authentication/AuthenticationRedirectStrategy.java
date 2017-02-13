package com.leadbank.guardian.client.authentication;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationRedirectStrategy {

    void redirect(HttpServletRequest request, HttpServletResponse response, String potentialRedirectUrl)
            throws IOException;

}
