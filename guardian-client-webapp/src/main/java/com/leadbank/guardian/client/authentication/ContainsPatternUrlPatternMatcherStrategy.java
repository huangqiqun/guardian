package com.leadbank.guardian.client.authentication;

public final class ContainsPatternUrlPatternMatcherStrategy implements UrlPatternMatcherStrategy {

    private String pattern;
    
    public boolean matches(final String url) {
        return url.contains(this.pattern);
    }

    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }
}
