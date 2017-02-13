package com.leadbank.guardian.facade.bean;

import java.util.Map;

public class AssertionResponse extends AbstractResponse {

    //用户
    private String user;
    //属性
    private Map<String, Object> attribute;
    //错误编码
    private String code;
    //错误信息
    private String description;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Map<String, Object> getAttribute() {
        return attribute;
    }

    public void setAttribute(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
