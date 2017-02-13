package com.leadbank.guardian.facade.bean;

import java.io.Serializable;

public abstract class AbstractRequest implements Serializable {

    private String ticket;

    private String service;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
