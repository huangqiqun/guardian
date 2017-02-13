package com.leadbank.guardian;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

public class GuardianHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final String sessionName;
    private final String sessionId;
    private final Integer timeout;

    public GuardianHttpServletRequestWrapper(HttpServletRequest request, String sessionName, String sessionId, Integer timeout) {
        super(request);
        this.sessionName = sessionName;
        this.sessionId = sessionId;
        this.timeout = timeout;
    }

    public HttpSession getSession(boolean create) {
        HttpSession session = super.getSession(create);
        if (session == null) {
            return null;
        }

        session.setMaxInactiveInterval(timeout);
        return new GuardianHttpSessionWrapper(session, sessionName, sessionId);
    }

    public HttpSession getSession() {
        HttpSession session = super.getSession();
        session.setMaxInactiveInterval(timeout);
        return new GuardianHttpSessionWrapper(session, sessionName, sessionId);
    }

}
