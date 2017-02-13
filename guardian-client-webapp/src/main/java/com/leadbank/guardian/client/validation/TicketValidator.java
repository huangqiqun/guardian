package com.leadbank.guardian.client.validation;

public interface TicketValidator {

	Assertion validate(String ticket, String service) throws TicketValidationException;
	
    Assertion validateFromDubbo(String ticket, String service) throws TicketValidationException;
}
