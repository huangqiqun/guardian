package com.leadbank.guardian.facade.bean;

public class AuthenticationResponse extends AbstractResponse {

    //ST
    private String ticket;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

}
