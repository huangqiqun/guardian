package com.leadbank.guardian.facade.code;

/**
 * 
 *  利得金融
 * Copyright (c) 2013-2015 leadbank,Inc.All Rights Reserved.
 */


/**
 * 
 * @author syx
 * @version 响应状态，响应码，响应消息
 */
public enum RespCode {
	
	//处理状态  提交成功 T 处理成功S 失败F 未知 U
	//响应状态
  	STATUS_S                          		("S", "成功"),
  	STATUS_F                         		("F", "失败"),
  	
  	SUCCESS                             	("000000","成功"), 
    FAILD                                   ("130001","失败"),
    
    
    //接口系统异常！
    ERROR									("999999","系统异常！请查看log日志记录！！！")
    ;
    
    
    
  
    
    private String key;
    private String value;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValueMsg(String msg) {
        return value = msg;
    }
    /**
     * @param key
     * @param value
     */
    private RespCode(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
}
