package com.leadbank.guardian.authentication;

import com.leadbank.guardian.authentication.principal.Principal;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface Authentication extends Serializable {

    Principal getPrincipal();

    Date getAuthenticatedDate();

    Map<String, Object> getAttributes();
}
