package com.leadbank.guardian.facade.bean;

public class AuthenticationRequest extends AbstractRequest {

    private String sessionName;

    private String tgc;

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getTgc() {
        return tgc;
    }

    public void setTgc(String tgc) {
        this.tgc = tgc;
    }

}
