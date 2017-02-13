package com.leadbank.guardian.client.authentication;

import java.util.regex.Pattern;

public final class RegexUrlPatternMatcherStrategy implements UrlPatternMatcherStrategy {

    private Pattern pattern;
    
    public boolean matches(final String url) {
        return this.pattern.matcher(url).find();
    }

    public void setPattern(final String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
}
