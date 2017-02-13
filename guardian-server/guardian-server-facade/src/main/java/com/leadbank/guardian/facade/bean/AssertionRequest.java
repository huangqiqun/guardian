package com.leadbank.guardian.facade.bean;

import java.util.Date;

public class AssertionRequest extends AbstractRequest {

	/** 请求编号 */
	private String reqId;
	/** 请求时间 */
	private Date dateTime;
	/** 请求系统编号 */
	private String id;
	
	private String loginUrl;
	
	private String errorUrl;

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

}
