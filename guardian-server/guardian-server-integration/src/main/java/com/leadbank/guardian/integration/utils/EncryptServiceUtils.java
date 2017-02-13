package com.leadbank.guardian.integration.utils;

import java.util.UUID;

import com.leadbank.eds.facade.EncryptFacade;
import com.leadbank.eds.facade.bean.DecryptReq;
import com.leadbank.eds.facade.bean.DecryptReqBean;
import com.leadbank.eds.facade.bean.DecryptResp;
import com.leadbank.eds.facade.bean.EncryptReq;
import com.leadbank.eds.facade.bean.EncryptReqBean;
import com.leadbank.eds.facade.bean.EncryptResp;

/**
 * @author zhenhua.lv
 * 2016年7月26日
 */
public class EncryptServiceUtils {
	
	//调用会员加解密系统
	private EncryptFacade encryptFacade;
	//解密
	public DecryptResp decode(String req){
		DecryptReq decryptreq = new DecryptReq();
		decryptreq.setReqId(UUID.randomUUID().toString());
		decryptreq.setSysId("ld130");
		decryptreq.setVersion("1.0.0");
		DecryptReqBean decryptReqBean = new DecryptReqBean();
		decryptReqBean.setCipherTextIndex(req);
		decryptReqBean.setEncrytype("DES3_ECB_PKCS5");
		decryptReqBean.setKeyIndex("DEFAULT_3DES");
		decryptreq.setDecryptReqBean(decryptReqBean);
		return this.encryptFacade.decrypt(decryptreq);
	}
	//加密
	public EncryptResp encrypt(String req){
		EncryptReq encryptReq = new EncryptReq();
		encryptReq.setReqId(UUID.randomUUID().toString());
		encryptReq.setSysId("ld130");
		encryptReq.setVersion("1.0.0");
		EncryptReqBean encryptReqBean = new EncryptReqBean();
		encryptReqBean.setEncrytype("DES3_ECB_PKCS5");
		encryptReqBean.setKeyIndex("DEFAULT_3DES");
		encryptReqBean.setPlainText(req);
		encryptReqBean.setIsSave("1");
		encryptReq.setEncryptReqBean(encryptReqBean);
		return this.encryptFacade.encrypt(encryptReq);
	}
	
	public void setEncryptFacade(EncryptFacade encryptFacade) {
		this.encryptFacade = encryptFacade;
	}
}
