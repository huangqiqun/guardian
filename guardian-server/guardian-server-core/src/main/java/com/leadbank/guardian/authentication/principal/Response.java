package com.leadbank.guardian.authentication.principal;

import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Response {

    private static final Pattern NON_PRINTABLE =
        Pattern.compile("[\\x00-\\x19\\x7F]+");
    
    protected static final Logger LOG = LoggerFactory.getLogger(Response.class);

    public static enum ResponseType {
        POST, REDIRECT
    }

    private final ResponseType responseType;

    private final String url;

    private final Map<String, String> attributes;

    protected Response(ResponseType responseType, final String url, final Map<String, String> attributes) {
        this.responseType = responseType;
        this.url = url;
        this.attributes = attributes;
    }

    public static Response getPostResponse(final String url, final Map<String, String> attributes) {
        return new Response(ResponseType.POST, url, attributes);
    }

    public static Response getRedirectResponse(final String url, final Map<String, String> parameters) {
        final StringBuilder builder = new StringBuilder(parameters.size() * 40 + 100);
        boolean isFirst = true;
        final String[] fragmentSplit = sanitizeUrl(url).split("#");
        
        builder.append(fragmentSplit[0]);
        
        for (final Map.Entry<String, String> entry : parameters.entrySet()) {
            if (entry.getValue() != null) {
                if (isFirst) {
                    builder.append(url.contains("?") ? "&" : "?");
                    isFirst = false;
                } else {
                    builder.append("&");   
                }
                builder.append(entry.getKey());
                builder.append("=");

                try {
                    builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (final Exception e) {
                    builder.append(entry.getValue());
                }
            }
        }

        if (fragmentSplit.length > 1) {
            builder.append("#");
            builder.append(fragmentSplit[1]);
        }

        return new Response(ResponseType.REDIRECT, builder.toString(), parameters);
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public ResponseType getResponseType() {
        return this.responseType;
    }

    public String getUrl() {
        return this.url;
    }
 
    private static String sanitizeUrl(final String url) {
        final Matcher m = NON_PRINTABLE.matcher(url);
        final StringBuffer sb = new StringBuffer(url.length());
        boolean hasNonPrintable = false;
        while (m.find()) {
            m.appendReplacement(sb, " ");
            hasNonPrintable = true;
        }
        m.appendTail(sb);
        if (hasNonPrintable) {
            LOG.warn("The following redirect URL has been sanitized and may be sign of attack:\n" + url);
        }
        return sb.toString();
    }
}
