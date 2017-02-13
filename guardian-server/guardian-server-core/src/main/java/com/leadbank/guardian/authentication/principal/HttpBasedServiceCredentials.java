package com.leadbank.guardian.authentication.principal;

import java.net.URL;

import org.springframework.util.Assert;

public class HttpBasedServiceCredentials implements Credentials {

    private final URL callbackUrl;

    private final String callbackUrlAsString;

    public HttpBasedServiceCredentials(final URL callbackUrl) {
        Assert.notNull(callbackUrl, "callbackUrl cannot be null");
        this.callbackUrl = callbackUrl;
        this.callbackUrlAsString = callbackUrl.toExternalForm();
    }

    public final URL getCallbackUrl() {
        return this.callbackUrl;
    }

    public final String toString() {
        return "[callbackUrl: " + this.callbackUrlAsString + "]";
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
            * result
            + ((this.callbackUrlAsString == null) ? 0 : this.callbackUrlAsString
                .hashCode());
        return result;
    }

    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final HttpBasedServiceCredentials other = (HttpBasedServiceCredentials) obj;
        if (this.callbackUrlAsString == null) {
            if (other.callbackUrlAsString != null)
                return false;
        } else if (!this.callbackUrlAsString.equals(other.callbackUrlAsString))
            return false;
        return true;
    }
    
}
