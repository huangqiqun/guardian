package com.leadbank.guardian;

import com.leadbank.common.context.ApplicationContextConfig;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class SessionFilter extends GenericFilterBean {

    private final String sessionName = "sid";
    private String cookiePath = "";

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final String name = ApplicationContextConfig.get("guardianSystemName");
        Assert.notNull(name, "name can not be null！");
        final String code = ApplicationContextConfig.get("guardianSystemCode");
        Assert.notNull(code, "code can not be null！");
        final Integer timeout = Integer.parseInt(ApplicationContextConfig.get("guardianSessionTimeout"));
        Assert.notNull(timeout, "timeout can not be null！");

        StringBuffer prefix = new StringBuffer("SESSION_").append(name).append("_").append(code).append("_");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!StringUtils.hasText(cookiePath)) {
            final String contextPath = request.getContextPath();
            cookiePath = StringUtils.hasText(contextPath) ? contextPath + "/" : "/";
        }

        String sessionId = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(sessionName)) {
                    sessionId = cookie.getValue();
                }
            }
        }

        if (!StringUtils.hasText(sessionId)) {
            sessionId = UUID.randomUUID().toString();

            Cookie cookie = new Cookie(sessionName, sessionId);
            cookie.setMaxAge(-1);
            cookie.setPath(this.cookiePath);
            response.addCookie(cookie);
        }

        final String sessionName = prefix.append(sessionId).toString();
        request.setAttribute("sessionName", sessionName);
        request.setAttribute("sessionId", sessionId);
        GuardianHttpServletRequestWrapper wrapper = new GuardianHttpServletRequestWrapper(request, sessionName, sessionId, timeout);
        filterChain.doFilter(wrapper, response);
    }

}
