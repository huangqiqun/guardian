package com.leadbank.guardian.authentication.principal;

public interface RememberMeCredentials extends Credentials {
    
    String AUTHENTICATION_ATTRIBUTE_REMEMBER_ME = "org.jasig.cas.authentication.principal.REMEMBER_ME";
    
    String REQUEST_PARAMETER_REMEMBER_ME = "rememberMe";

    boolean isRememberMe();
    
    void setRememberMe(boolean rememberMe);
}
