package com.leadbank.guardian.client.validation;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.leadbank.guardian.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.leadbank.guardian.client.ssl.HttpURLConnectionFactory;
import com.leadbank.guardian.client.ssl.HttpsURLConnectionFactory;
import com.leadbank.guardian.client.util.CommonUtils;
import com.leadbank.guardian.facade.bean.AssertionRequest;
import com.leadbank.guardian.facade.bean.AssertionResponse;
import com.leadbank.guardian.facade.validate.ServiceValidateFacade;

public abstract class AbstractUrlBasedTicketValidator implements TicketValidator {
	
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private ServiceValidateFacade serviceValidateFacade;

    private HttpURLConnectionFactory urlConnectionFactory = new HttpsURLConnectionFactory();

    private final String guardianServerUrlPrefix;

    private Map<String, String> customParameters;

    private String encoding;
    
    protected AbstractUrlBasedTicketValidator(final String guardianServerUrlPrefix) {
        this.guardianServerUrlPrefix = guardianServerUrlPrefix;
        CommonUtils.assertNotNull(this.guardianServerUrlPrefix, "guardianServerUrlPrefix cannot be null.");
    }

    protected void populateUrlAttributeMap(final Map<String, String> urlParameters) {
        // nothing to do
    }

    protected abstract String getUrlSuffix();

    protected abstract void setDisableXmlSchemaValidation(boolean disabled);

    protected final String constructValidationUrl(final String ticket, final String serviceUrl) {
        final Map<String, String> urlParameters = new HashMap<String, String>();

        logger.debug("Placing URL parameters in map.");
        urlParameters.put("ticket", ticket);
        urlParameters.put("service", serviceUrl);

        logger.debug("Calling template URL attribute map.");
        populateUrlAttributeMap(urlParameters);

        logger.debug("Loading custom parameters from configuration.");
        if (this.customParameters != null) {
            urlParameters.putAll(this.customParameters);
        }

        final String suffix = getUrlSuffix();
        final StringBuilder buffer = new StringBuilder(urlParameters.size() * 10 + this.guardianServerUrlPrefix.length()
                + suffix.length() + 1);

        int i = 0;

        buffer.append(this.guardianServerUrlPrefix);
        if (!this.guardianServerUrlPrefix.endsWith("/")) {
            buffer.append("/");
        }
        buffer.append(suffix);

        for (Map.Entry<String, String> entry : urlParameters.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();

            if (value != null) {
                buffer.append(i++ == 0 ? "?" : "&");
                buffer.append(key);
                buffer.append("=");
                final String encodedValue = encodeUrl(value);
                buffer.append(encodedValue);
            }
        }

        return buffer.toString();

    }

    protected final String encodeUrl(final String url) {
        if (url == null) {
            return null;
        }

        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            return url;
        }
    }

    protected abstract Assertion parseResponseFromServer(final String response) throws TicketValidationException;
    
    protected abstract Assertion parseResponseFromServer(final AssertionResponse response) throws TicketValidationException;

    protected abstract String retrieveResponseFromServer(URL validationUrl, String ticket);

    public final Assertion validate(final String ticket, final String service) throws TicketValidationException {
        final String validationUrl = constructValidationUrl(ticket, service);
        logger.debug("Constructing validation url: {}", validationUrl);

        try {
            logger.debug("Retrieving response from server.");
            final String serverResponse = retrieveResponseFromServer(new URL(validationUrl), ticket);

            if (serverResponse == null) {
                throw new TicketValidationException("The Guardian server returned no response.");
            }

            logger.debug("Server response: {}", serverResponse);

            return parseResponseFromServer(serverResponse);
        } catch (final MalformedURLException e) {
            throw new TicketValidationException(e);
        }
    }
    
    public final Assertion validateFromDubbo(final String ticket, final String service) throws TicketValidationException {
        AssertionRequest request = new AssertionRequest();
        //TODO
        request.setId("");
        request.setTicket(ticket);
        request.setService(service);
        request.setDateTime(new Date());
        try {
            logger.debug("Retrieving response from server.");
            this.serviceValidateFacade = (ServiceValidateFacade) SpringContextUtil.getBean("serviceValidateFacade");
            AssertionResponse response = this.serviceValidateFacade.validate(request);
            if (response == null) {
                throw new TicketValidationException("The Guardian server returned no response.");
            }

            logger.debug("Server response: {}", response);
            
            return parseResponseFromServer(response);
        } catch (final Exception e) {
            throw new TicketValidationException(e);
        }
    }

    public final void setCustomParameters(final Map<String, String> customParameters) {
        this.customParameters = customParameters;
    }

    public final void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    protected final String getEncoding() {
        return this.encoding;
    }

    protected final String getGuardianServerUrlPrefix() {
        return this.guardianServerUrlPrefix;
    }

    protected final Map<String, String> getCustomParameters() {
        return this.customParameters;
    }

    protected HttpURLConnectionFactory getURLConnectionFactory() {
        return this.urlConnectionFactory;
    }

    public void setURLConnectionFactory(final HttpURLConnectionFactory urlConnectionFactory) {
        this.urlConnectionFactory = urlConnectionFactory;
    }

}
