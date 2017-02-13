package com.leadbank.guardian.client.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.leadbank.guardian.client.ssl.HttpURLConnectionFactory;
import com.leadbank.guardian.client.ssl.HttpsURLConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    private static final HttpURLConnectionFactory DEFAULT_URL_CONNECTION_FACTORY = new HttpsURLConnectionFactory();

    private CommonUtils() {
        // nothing to do
    }

    public static String formatForUtcTime(final Date date) {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public static void assertNotNull(final Object object, final String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertNotEmpty(final Collection<?> c, final String message) {
        assertNotNull(c, message);
        if (c.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertTrue(final boolean cond, final String message) {
        if (!cond) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isEmpty(final String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isNotEmpty(final String string) {
        return !isEmpty(string);
    }

    public static boolean isBlank(final String string) {
        return isEmpty(string) || string.trim().length() == 0;
    }

    public static boolean isNotBlank(final String string) {
        return !isBlank(string);
    }

    public static String constructRedirectUrl(final String casServerLoginUrl, final String serviceParameterName,
            final String serviceUrl) {
        return casServerLoginUrl + (casServerLoginUrl.contains("?") ? "&" : "?") + serviceParameterName + "="
                + urlEncode(serviceUrl);
    }

    public static String constructRedirectUrl(final String casServerLoginUrl, final String serviceParameterName,
            final String serviceUrl, final String originalUrl) {
        return casServerLoginUrl + (casServerLoginUrl.contains("?") ? "&" : "?") + serviceParameterName + "="
                + urlEncode(serviceUrl) + "&originalUrl=" + urlEncode(originalUrl);
    }

    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String findMatchingServerName(final HttpServletRequest request, final String serverName) {
        final String[] serverNames = serverName.split(" ");

        if (serverNames == null || serverNames.length == 0 || serverNames.length == 1) {
            return serverName;
        }

        final String host = request.getHeader("Host");
        final String xHost = request.getHeader("X-Forwarded-Host");

        final String comparisonHost;
        if (xHost != null && host == "localhost") {
            comparisonHost = xHost;
        } else {
            comparisonHost = host;
        }

        if (comparisonHost == null) {
            return serverName;
        }

        for (final String server : serverNames) {
            final String lowerCaseServer = server.toLowerCase();

            if (lowerCaseServer.contains(comparisonHost)) {
                return server;
            }
        }

        return serverNames[0];
    }

    private static boolean serverNameContainsPort(final boolean containsScheme, final String serverName) {
        if (!containsScheme && serverName.contains(":")) {
            return true;
        }

        final int schemeIndex = serverName.indexOf(":");
        final int portIndex = serverName.lastIndexOf(":");
        return schemeIndex != portIndex;
    }

    private static boolean requestIsOnStandardPort(final HttpServletRequest request) {
        final int serverPort = request.getServerPort();
        return serverPort == 80 || serverPort == 443;
    }

    public static String constructServiceUrl(final HttpServletRequest request, final HttpServletResponse response,
            final String serverName) {
        return serverName;
    }

    public static String constructServiceUrl(final HttpServletRequest request, final HttpServletResponse response,
            final String service, final String serverNames, final String artifactParameterName, final boolean encode) {
        if (CommonUtils.isNotBlank(service)) {
            return encode ? response.encodeURL(service) : service;
        }

        final StringBuilder buffer = new StringBuilder();

        final String serverName = findMatchingServerName(request, serverNames);

        boolean containsScheme = true;
        if (!serverName.startsWith("https://") && !serverName.startsWith("http://")) {
            buffer.append(request.isSecure() ? "https://" : "http://");
            containsScheme = false;
        }

        buffer.append(serverName);

        if (!serverNameContainsPort(containsScheme, serverName) && !requestIsOnStandardPort(request)) {
            buffer.append(":");
            buffer.append(request.getServerPort());
        }

        buffer.append(request.getRequestURI());

        if (CommonUtils.isNotBlank(request.getQueryString())) {
            final int location = request.getQueryString().indexOf(artifactParameterName + "=");

            if (location == 0) {
                final String returnValue = encode ? response.encodeURL(buffer.toString()) : buffer.toString();
                LOGGER.debug("serviceUrl generated: {}", returnValue);
                return returnValue;
            }

            buffer.append("?");

            if (location == -1) {
                buffer.append(request.getQueryString());
            } else if (location > 0) {
                final int actualLocation = request.getQueryString().indexOf("&" + artifactParameterName + "=");

                if (actualLocation == -1) {
                    buffer.append(request.getQueryString());
                } else if (actualLocation > 0) {
                    buffer.append(request.getQueryString().substring(0, actualLocation));
                }
            }
        }

        final String returnValue = encode ? response.encodeURL(buffer.toString()) : buffer.toString();
        LOGGER.debug("serviceUrl generated: {}", returnValue);
        return returnValue;
    }

    public static String safeGetParameter(final HttpServletRequest request, final String parameter,
            final List<String> parameters) {
        if ("POST".equals(request.getMethod()) && parameters.contains(parameter)) {
            LOGGER.debug("safeGetParameter called on a POST HttpServletRequest for Restricted Parameters.  Cannot complete check safely.  Reverting to standard behavior for this Parameter");
            return request.getParameter(parameter);
        }
        return request.getQueryString() == null || !request.getQueryString().contains(parameter) ? null : request
                .getParameter(parameter);
    }

    public static String safeGetParameter(final HttpServletRequest request, final String parameter) {
        return safeGetParameter(request, parameter, Arrays.asList("logoutRequest"));
    }

    public static String getResponseFromServer(final URL constructedUrl, final HttpURLConnectionFactory factory,
            final String encoding) {

        HttpURLConnection conn = null;
        InputStreamReader in = null;
        try {
            conn = factory.buildHttpURLConnection(constructedUrl.openConnection());

            if (CommonUtils.isEmpty(encoding)) {
                in = new InputStreamReader(conn.getInputStream());
            } else {
                in = new InputStreamReader(conn.getInputStream(), encoding);
            }

            final StringBuilder builder = new StringBuilder(255);
            int byteRead;
            while ((byteRead = in.read()) != -1) {
                builder.append((char) byteRead);
            }

            return builder.toString();
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            closeQuietly(in);
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static void sendRedirect(final HttpServletResponse response, final String url) {
        try {
            response.sendRedirect(url);
        } catch (final Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

    }

    public static void closeQuietly(final Closeable resource) {
        try {
            if (resource != null) {
                resource.close();
            }
        } catch (final IOException e) {
            //ignore
        }
    }
}
