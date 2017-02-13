package com.leadbank.guardian.authentication.handler.support;

import com.leadbank.guardian.authentication.handler.AuthenticationException;
import com.leadbank.guardian.authentication.principal.UsernamePasswordCredentials;
import com.leadbank.guardian.constant.RedisKey;
import com.leadbank.guardian.facade.code.RespCode;
import com.leadbank.guardian.integration.constant.Constant;
import com.leadbank.guardian.integration.mcs.MCSAuthenticateFacade;
import com.leadbank.guardian.integration.utils.EncDecUtils;
import com.leadbank.guardian.integration.utils.EncryptServiceUtils;
import com.leadbank.guardian.util.ObjectSerializeUtil;
import com.leadbank.mcs.facade.bean.AuthenticateReq;
import com.leadbank.mcs.facade.bean.AuthenticateResp;
import redis.clients.jedis.JedisCluster;

import java.util.UUID;

public final class MCSUsernamePasswordAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
	
	private MCSAuthenticateFacade mcsAuthenticateFacade;
	
	private EncryptServiceUtils encryptServiceUtils;
	
	private JedisCluster jedisCluster;

	public boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials credentials) throws AuthenticationException {
		final String username = credentials.getUsername();
		final String password = credentials.getPassword();
		//认证逻辑
		AuthenticateReq authenticateReq = new AuthenticateReq();
		authenticateReq.setSysId(Constant.GUARDIANSERVICE_SYSTEM_ID);//系统ID
		authenticateReq.setReqId(UUID.randomUUID().toString());
		authenticateReq.setAppId(Constant.APP_ID);//应用ID WEB 1002
		authenticateReq.setVersion(Constant.MCS_VERSION);
		authenticateReq.setGrantType("password");//认证类型
		authenticateReq.setChannelCode("LD");//渠道编码
		try {
			authenticateReq.setLoginAccount(encryptServiceUtils.encrypt(username).getEncryptRespBean().getCipherTextIndex());//帐号加密
			authenticateReq.setPassword(EncDecUtils.getMD5(password));//密码 md5加密
			AuthenticateResp accessTokenResp = this.mcsAuthenticateFacade.getAccessToken(authenticateReq);
			jedisCluster.hset(RedisKey.ACCESSTOKEN, String.valueOf(username), ObjectSerializeUtil.serialize(accessTokenResp));
			if(accessTokenResp.getStatus().equals(RespCode.STATUS_S.getKey())){
				log.debug("User [" + username + "] was successfully authenticated.");
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			log.debug("User [" + username + "] failed authentication");
			return false;
		}
	}

	public void setMcsAuthenticateFacade(MCSAuthenticateFacade mcsAuthenticateFacade) {
		this.mcsAuthenticateFacade = mcsAuthenticateFacade;
	}

	public void setEncryptServiceUtils(EncryptServiceUtils encryptServiceUtils) {
		this.encryptServiceUtils = encryptServiceUtils;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}
	
}
