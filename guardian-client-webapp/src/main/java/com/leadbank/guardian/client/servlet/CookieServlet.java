package com.leadbank.guardian.client.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

public class CookieServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("P3P", "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"");
		final String method = request.getParameter("method");

		Cookie cookie = null;
		if ("add".equals(method)) {
			final String contextPath = request.getContextPath();
			final String cookiePath = StringUtils.hasText(contextPath) ? contextPath + "/" : "/";
			String guardianTGC = request.getParameter("guardianTGC");
			cookie = new Cookie("GuardianTGC", guardianTGC);
			cookie.setPath(cookiePath);
			cookie.setMaxAge(-1);
		} else if ("remove".equals(method)) {
			cookie = new Cookie("GuardianTGC", null);
			cookie.setMaxAge(0);
		}
		response.addCookie(cookie);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
}
