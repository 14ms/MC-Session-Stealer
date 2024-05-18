package com.github.shurpe;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;

public final class HttpUtils {

    public static String getContentAsString(final String url) throws Exception {
        try (final CloseableHttpClient httpClient = createUntrustedClient()) {
            final HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");
            final HttpResponse httpResponse = httpClient.execute(httpGet);

            return EntityUtils.toString(httpResponse.getEntity());
        }
    }

    // fix for javax.net.ssl.SSLHandshakeException
    private static CloseableHttpClient createUntrustedClient() throws Exception {
        final SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (x509CertChain, authType) -> true)
                .build();

        return HttpClientBuilder.create()
                .setSslcontext(sslContext)
                .disableContentCompression()
                .setHostnameVerifier(new AllowAllHostnameVerifier())
                .build();
    }
}