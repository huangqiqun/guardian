package com.leadbank.guardian.integration.mcs.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leadbank.guardian.integration.mcs.MCSAuthenticateFacade;
import com.leadbank.mcs.facade.AccessTokenFacade;
import com.leadbank.mcs.facade.AuthenticateFacade;
import com.leadbank.mcs.facade.LogoutFacade;
import com.leadbank.mcs.facade.bean.AccessTokenReq;
import com.leadbank.mcs.facade.bean.AccessTokenResp;
import com.leadbank.mcs.facade.bean.AuthenticateReq;
import com.leadbank.mcs.facade.bean.AuthenticateResp;
import com.leadbank.mcs.facade.bean.LogoutReq;
import com.leadbank.mcs.facade.bean.LogoutResp;

public class MCSAuthenticateFacadeImpl implements MCSAuthenticateFacade {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 用户认证
	private AuthenticateFacade authenticateFacade;

	// token验证
	private AccessTokenFacade accessTokenFacade;

	// 登出
	private LogoutFacade logoutFacade;

	// 调用认证中心
	public AuthenticateResp getAccessToken(AuthenticateReq req) {
		AuthenticateResp accessToken = new AuthenticateResp();
		try {
			accessToken = this.authenticateFacade.getAccessToken(req);
		} catch (Exception e) {
			logger.error("调用dubbo服务异常", e);
		}
		return accessToken;
	}

	public Object verifyAccessToken(AccessTokenReq accessTokenReq) {
		AccessTokenResp checkAccessToken = new AccessTokenResp();
		try {
			checkAccessToken = this.accessTokenFacade.checkAccessToken(accessTokenReq);
		} catch (Exception e) {
			logger.error("调用dubbo服务异常", e);
		}
		return checkAccessToken;
	}

	public Object logout(LogoutReq logoutReq) {
		LogoutResp logoutResp = new LogoutResp();
		try {
			logoutResp = this.logoutFacade.logout(logoutReq);
		} catch (Exception e) {
			logger.error("调用dubbo服务异常", e);
		}
		return logoutResp;
	}

	public Object refreshToken(AccessTokenReq accessTokenReq) {
		//TODO
		return null;
	}


	public void setAuthenticateFacade(AuthenticateFacade authenticateFacade) {
		this.authenticateFacade = authenticateFacade;
	}
	
	public void setAccessTokenFacade(AccessTokenFacade accessTokenFacade) {
		this.accessTokenFacade = accessTokenFacade;
	}
	
	public void setLogoutFacade(LogoutFacade logoutFacade) {
		this.logoutFacade = logoutFacade;
	}
}
