package cn.laclab.client.finaldownload.core;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.laclab.client.finaldownload.core.callback.RequestCallBack;
import cn.laclab.client.finaldownload.core.http.HttpHandler;
import cn.laclab.client.finaldownload.core.http.HttpMethod;
import cn.laclab.client.finaldownload.core.http.RequestParams;
import cn.laclab.client.finaldownload.core.task.PriorityExecutor;
import cn.laclab.client.finaldownload.download.DownloadInfo;

/**
 * Created by sinye on 15/10/28.
 */
public class HttpUtils {
    private String responseTextCharset = "UTF-8";
    private final static int DEFAULT_CONN_TIMEOUT = 1000 * 15; // 15s
    private final static int DEFAULT_RETRY_TIMES = 3;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
    private final static int DEFAULT_POOL_SIZE = 2;
    private final static PriorityExecutor EXECUTOR = new PriorityExecutor(DEFAULT_POOL_SIZE);

    private int timeOut;
    private String userAgent;

    public HttpUtils(int connTimeout, String userAgent) {
        this.timeOut = connTimeout;
        this.userAgent = userAgent;
    }

    public HttpUtils() {
        this(HttpUtils.DEFAULT_CONN_TIMEOUT, null);
    }

    public HttpUtils(int connTimeout) {
        this(connTimeout, null);
    }

    public HttpUtils(String userAgent) {
        this(HttpUtils.DEFAULT_CONN_TIMEOUT, userAgent);
    }

    public HttpHandler<File> download(String url, String target,
                                      boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
        return download("GET", url, target, null, autoResume, autoRename, callback);
    }

    public HttpHandler<File> download(DownloadInfo info, RequestCallBack<File> callback) {
        if (info.getDownloadUrl() == null) throw new IllegalArgumentException("url may not be null");
        if (info.getFileSavePath()== null) throw new IllegalArgumentException("target may not be null");

        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(info.getDownloadUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeOut);
            connection.setRequestMethod(HttpMethod.GET.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHandler<File> handler = new HttpHandler<File>(connection, responseTextCharset, callback);
        //todo add header params
//        if (params != null) {
//            request.setRequestParams(params, handler);
//            handler.setPriority(params.getPriority());
//        }
        handler.executeOnExecutor(EXECUTOR, connection, info.getFileSavePath(), info.isAutoResume(), info.isAutoRename());
        return handler;
    }

    public HttpHandler<File> download(String method, String murl, String target,
                                      RequestParams params, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {

        if (murl == null) throw new IllegalArgumentException("url may not be null");
        if (target == null) throw new IllegalArgumentException("target may not be null");

        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(murl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeOut);
            connection.setRequestMethod(HttpMethod.GET.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHandler<File> handler = new HttpHandler<File>(connection, responseTextCharset, callback);

        //todo add header params
//        if (params != null) {
//            request.setRequestParams(params, handler);
//            handler.setPriority(params.getPriority());
//        }
        handler.executeOnExecutor(EXECUTOR, connection, target, autoResume, autoRename);
        return handler;
    }
}
