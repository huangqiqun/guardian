package com.leadbank.guardian.services;

import com.leadbank.guardian.authentication.principal.Service;

import java.io.Serializable;
import java.util.List;

public interface RegisteredService extends Cloneable, Serializable {

    boolean isEnabled();

    boolean isAnonymousAccess();
    
    boolean isIgnoreAttributes();

    List<String> getAllowedAttributes();

    boolean isAllowedToProxy();

    String getServiceId();

    long getId();

    String getName();

    String getTheme();

    boolean isSsoEnabled();

    String getDescription();
   
    int getEvaluationOrder();

    boolean matches(final Service service);
    
    Object clone() throws CloneNotSupportedException;
}
