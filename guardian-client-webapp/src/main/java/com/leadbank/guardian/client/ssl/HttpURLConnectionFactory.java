package com.leadbank.guardian.client.ssl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public interface HttpURLConnectionFactory {

    HttpURLConnection buildHttpURLConnection(final URLConnection url);
}
