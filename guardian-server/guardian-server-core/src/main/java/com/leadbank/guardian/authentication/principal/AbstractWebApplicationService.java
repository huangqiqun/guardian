package com.leadbank.guardian.authentication.principal;

import com.leadbank.guardian.util.DefaultUniqueTicketIdGenerator;
import com.leadbank.guardian.util.HttpClient;
import com.leadbank.guardian.util.SpringContextUtil;
import com.leadbank.guardian.util.UniqueTicketIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractWebApplicationService implements WebApplicationService {

    protected static final Logger LOG = LoggerFactory.getLogger(WebApplicationService.class);
    
    private static final Map<String, Object> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<String, Object>());
    
    private static final UniqueTicketIdGenerator GENERATOR = new DefaultUniqueTicketIdGenerator();
    
    private final String id;
    
    private final String originalUrl;

    private final String artifactId;
    
    private Principal principal;
    
    private boolean loggedOutAlready = false;
    
    private final HttpClient httpClient;
    
    protected AbstractWebApplicationService(final String id, final String originalUrl, final String artifactId, final HttpClient httpClient) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.artifactId = artifactId;
        this.httpClient = httpClient;
    }
    
    public final String toString() {
        return this.id;
    }
    
    public final String getId() {
        return this.id;
    }
    
    public final String getArtifactId() {
        return this.artifactId;
    }

    public final Map<String, Object> getAttributes() {
        return EMPTY_MAP;
    }

    protected static String cleanupUrl(final String url) {
        if (url == null) {
            return null;
        }

        final int jsessionPosition = url.indexOf(";sid");

        if (jsessionPosition == -1) {
            return url;
        }

        final int questionMarkPosition = url.indexOf("?");

        if (questionMarkPosition < jsessionPosition) {
            return url.substring(0, url.indexOf(";sid"));
        }

        return url.substring(0, jsessionPosition)
            + url.substring(questionMarkPosition);
    }
    
    protected final String getOriginalUrl() {
        return this.originalUrl;
    }

    protected final HttpClient getHttpClient() {
        return this.httpClient;
    }

    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Service) {
            final Service service = (Service) object;

            return getId().equals(service.getId());
        }

        return false;
    }
    
    public int hashCode() {
        final int prime = 41;
        int result = 1;
        result = prime * result
            + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }
    
    protected Principal getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(final Principal principal) {
        this.principal = principal;
    }
    
    public boolean matches(final Service service) {
        return this.id.equals(service.getId());
    }
    
    public synchronized boolean logOutOfService(final String sessionIdentifier) {
        if (this.loggedOutAlready) {
            return true;
        }

        LOG.debug("Sending logout request for: " + getId());

        final String logoutRequest = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ID=\""
            + GENERATOR.getNewTicketId("LR")
            + "\" Version=\"2.0\" IssueInstant=\""
            + "\"><saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">@NOT_USED@</saml:NameID><samlp:SessionIndex>"
            + sessionIdentifier + "</samlp:SessionIndex></samlp:LogoutRequest>";
        
        this.loggedOutAlready = true;
        
        if (this.httpClient != null) {
            return this.httpClient.sendMessageToEndPoint(getOriginalUrl(), logoutRequest, true);
        }
        
        return false;
    }
    
    public synchronized void logOutOfServiceRedis(final String sessionName) {
    	if (this.loggedOutAlready) {
    		return;
    	}
    	
    	LOG.debug("Sending logout request for: " + getId());
    	
        this.loggedOutAlready = true;

        final JedisCluster jedisCluster = (JedisCluster) SpringContextUtil.getBean("jedisCluster");
        for (String key : jedisCluster.hkeys(sessionName)) {
            if (!"sessionId".equals(key)) {
                jedisCluster.hdel(sessionName, key);
            }
        }
    }
}
