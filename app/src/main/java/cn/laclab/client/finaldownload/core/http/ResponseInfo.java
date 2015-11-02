package cn.laclab.client.finaldownload.core.http;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ResponseInfo<T> {

    private HttpURLConnection conn;
    public T result;
    public final boolean resultFormCache;

    public Locale locale;

    // status line
    public final int statusCode;

    // entity
    public final long contentLength;
    public final String contentEncoding;

    public Map<String,List<String>> getAllHeaders() {
        if (conn == null) return null;
        return conn.getHeaderFields();
    }

    public String getHeaders(String name) {
        if (conn == null) return null;
        return conn.getHeaderField(name);
    }

    public String getFirstHeader(String name) {
        if (conn == null) return null;
        return conn.getHeaderField(name);
    }

    public String getLastHeader(String name) {
        if (conn == null) return null;
        return conn.getHeaderField(name);
    }

    public ResponseInfo(final HttpURLConnection conn, T result, boolean resultFormCache) {
        this.conn = conn;
        this.result = result;
        this.resultFormCache = resultFormCache;

        if (conn != null) {
//            locale = conn.getLocale();
            // status line
            int responseCode = 0;
            try {
                responseCode = conn.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseCode > 0) {
                statusCode = responseCode;
//                protocolVersion = statusLine.getProtocolVersion();
//                reasonPhrase = statusLine.getReasonPhrase();
            } else {
                statusCode = 0;
//                protocolVersion = null;
//                reasonPhrase = null;
            }

            contentLength = conn.getContentLength();
            contentEncoding = conn.getContentEncoding();
            // entity
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                contentLength = entity.getContentLength();
//                contentType = entity.getContentType();
//                contentEncoding = entity.getContentEncoding();
//            } else {
//                contentLength = 0;
//                contentType = null;
//                contentEncoding = null;
//            }
        } else {
            locale = null;

            // status line
            statusCode = 0;
            // entity
            contentLength = 0;
            contentEncoding = null;
        }
    }
}
