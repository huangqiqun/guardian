package com.leadbank.guardian.client.validation;

import com.leadbank.guardian.client.authentication.AttributePrincipal;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface Assertion extends Serializable {

    Date getValidFromDate();

    Date getValidUntilDate();

    Date getAuthenticationDate();

    Map<String, Object> getAttributes();

    AttributePrincipal getPrincipal();

    boolean isValid();
}
