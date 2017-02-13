package com.leadbank.guardian.client.authentication;

public final class ExactUrlPatternMatcherStrategy implements UrlPatternMatcherStrategy {

    private String pattern;
    
    public boolean matches(final String url) {
        return url.equals(this.pattern);
    }

    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

}
