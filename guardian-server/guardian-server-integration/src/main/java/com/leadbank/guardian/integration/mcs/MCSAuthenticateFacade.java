package com.leadbank.guardian.integration.mcs;

import com.leadbank.mcs.facade.bean.AccessTokenReq;
import com.leadbank.mcs.facade.bean.AuthenticateReq;
import com.leadbank.mcs.facade.bean.AuthenticateResp;
import com.leadbank.mcs.facade.bean.LogoutReq;

public interface MCSAuthenticateFacade {
	
	AuthenticateResp getAccessToken(AuthenticateReq authenticateReq);

	Object verifyAccessToken(AccessTokenReq accessTokenReq);

	Object refreshToken(AccessTokenReq accessTokenReq);
	
	Object logout(LogoutReq logoutReq);
}
