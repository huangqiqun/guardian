package com.leadbank.guardian.facade.code;

/**
 * 
 *  利得金融
 * Copyright (c) 2013-2015 leadbank,Inc.All Rights Reserved.
 */

/**
 * 
 * @author syx
 * @version cmu变量
 */
public enum SystemCode {
    
	//状态
	STATUS_TRUE							("TRUE", "成功"),
	STATUS_FALSE						("FALSE", "失败"),
	//是否有效
	IS_VALID_Y							("Y", "是"),
	IS_VALID_N							("N", "否"),
	
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
    /**
     * @param key
     * @param value
     */
    private SystemCode(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
}
