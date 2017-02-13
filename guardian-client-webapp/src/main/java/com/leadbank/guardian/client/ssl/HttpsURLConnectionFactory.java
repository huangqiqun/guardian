package com.leadbank.guardian.client.ssl;

import com.leadbank.guardian.client.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.Properties;

public final class HttpsURLConnectionFactory implements HttpURLConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpsURLConnectionFactory.class);

    private HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

    private Properties sslConfiguration = new Properties();

    public HttpsURLConnectionFactory() {
    }

    public HttpsURLConnectionFactory(final HostnameVerifier verifier, final Properties config) {
        setHostnameVerifier(verifier);
        setSSLConfiguration(config);
    }

    public final void setSSLConfiguration(final Properties config) {
        this.sslConfiguration = config;
    }

    public final void setHostnameVerifier(final HostnameVerifier verifier) {
        this.hostnameVerifier = verifier;
    }

    public HttpURLConnection buildHttpURLConnection(final URLConnection url) {
        return this.configureHttpsConnectionIfNeeded(url);
    }

    private HttpURLConnection configureHttpsConnectionIfNeeded(final URLConnection conn) {
        if (conn instanceof HttpsURLConnection) {
            final HttpsURLConnection httpsConnection = (HttpsURLConnection) conn;
            final SSLSocketFactory socketFactory = this.createSSLSocketFactory();
            if (socketFactory != null) {
                httpsConnection.setSSLSocketFactory(socketFactory);
            }

            if (this.hostnameVerifier != null) {
                httpsConnection.setHostnameVerifier(this.hostnameVerifier);
            }
        }
        return (HttpURLConnection) conn;
    }

    private SSLSocketFactory createSSLSocketFactory() {
        InputStream keyStoreIS = null;
        try {
            final SSLContext sslContext = SSLContext.getInstance(this.sslConfiguration.getProperty("protocol", "SSL"));

            if (this.sslConfiguration.getProperty("keyStoreType") != null) {
                final KeyStore keyStore = KeyStore.getInstance(this.sslConfiguration.getProperty("keyStoreType"));
                if (this.sslConfiguration.getProperty("keyStorePath") != null) {
                    keyStoreIS = new FileInputStream(this.sslConfiguration.getProperty("keyStorePath"));
                    if (this.sslConfiguration.getProperty("keyStorePass") != null) {
                        keyStore.load(keyStoreIS, this.sslConfiguration.getProperty("keyStorePass").toCharArray());
                        LOGGER.debug("Keystore has {} keys", keyStore.size());
                        final KeyManagerFactory keyManager = KeyManagerFactory.getInstance(this.sslConfiguration
                                .getProperty("keyManagerType", "SunX509"));
                        keyManager.init(keyStore, this.sslConfiguration.getProperty("certificatePassword")
                                .toCharArray());
                        sslContext.init(keyManager.getKeyManagers(), null, null);
                        return sslContext.getSocketFactory();
                    }
                }
            }

        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            CommonUtils.closeQuietly(keyStoreIS);
        }
        return null;
    }

}
