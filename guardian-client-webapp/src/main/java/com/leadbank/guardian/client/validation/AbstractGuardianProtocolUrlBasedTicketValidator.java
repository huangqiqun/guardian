package com.leadbank.guardian.client.validation;

import com.leadbank.guardian.client.util.CommonUtils;

import java.net.URL;

public abstract class AbstractGuardianProtocolUrlBasedTicketValidator extends AbstractUrlBasedTicketValidator {

    protected AbstractGuardianProtocolUrlBasedTicketValidator(final String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    protected final void setDisableXmlSchemaValidation(final boolean disable) {
        // nothing to do
    }

    protected final String retrieveResponseFromServer(final URL validationUrl, final String ticket) {
        return CommonUtils.getResponseFromServer(validationUrl, getURLConnectionFactory(), getEncoding());
    }
}
