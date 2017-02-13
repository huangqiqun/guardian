package com.leadbank.guardian.client.authentication;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

public interface AttributePrincipal extends Principal, Serializable {

    Map<String, Object> getAttributes();

}
