package com.leadbank.guardian.client.authentication;

public interface UrlPatternMatcherStrategy {

    boolean matches(String url);
    
    void setPattern(String pattern);
}
