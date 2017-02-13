package com.leadbank.guardian.validation;

import com.leadbank.guardian.authentication.Authentication;
import com.leadbank.guardian.authentication.principal.Service;

import java.io.Serializable;
import java.util.List;

public interface Assertion extends Serializable {

    List<Authentication> getChainedAuthentications();

    boolean isFromNewLogin();

    Service getService();

}
