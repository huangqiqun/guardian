package com.leadbank.guardian.authentication.principal;

public interface Service extends Principal {
    
    void setPrincipal(Principal principal);
    
    boolean logOutOfService(String sessionIdentifier);
    
    void logOutOfServiceRedis(String sessionName);
    
    boolean matches(Service service);
}
