package com.leadbank.guardian.facade.validate;

import com.leadbank.guardian.facade.bean.AuthenticationRequest;
import com.leadbank.guardian.facade.bean.AuthenticationResponse;

public interface AuthenticationFacade {

    AuthenticationResponse authenticate(AuthenticationRequest req) throws Exception;

}
