package cn.laclab.client.finaldownload.core.http;

import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.laclab.client.finaldownload.core.callback.FileDownloadHandler;
import cn.laclab.client.finaldownload.core.callback.RequestCallBack;
import cn.laclab.client.finaldownload.core.callback.RequestCallBackHandler;
import cn.laclab.client.finaldownload.core.exception.HttpException;
import cn.laclab.client.finaldownload.core.task.PriorityAsyncTask;
import cn.laclab.client.finaldownload.core.util.OtherUtils;


public class HttpHandler<T> extends PriorityAsyncTask<Object, Object, Void> implements RequestCallBackHandler {
    private HttpURLConnection conn = null;

    private URL requestUrl;
    private String requestMethod;
    private boolean isUploading = true;
    private RequestCallBack<T> callback;

    private int retriedCount = 0;
    private String fileSavePath = null;
    private boolean isDownloadingFile = false;
    private boolean autoResume = false; // Whether the downloading could continue from the point of interruption.
    private boolean autoRename = false; // Whether rename the file by response header info when the download completely.
    private String charset; // The default charset of response header info.

    public HttpHandler(HttpURLConnection conn, String charset, RequestCallBack<T> callback) {
        this.conn = conn;
        this.callback = callback;
        this.charset = charset;
    }

    private State state = State.WAITING;

    public State getState() {
        return state;
    }

//    private long expiry = conn.getExpiration();

//    public void setExpiry(long expiry) {
//        this.expiry = expiry;
//    }

    public void setRequestCallBack(RequestCallBack<T> callback) {
        this.callback = callback;
    }

    public RequestCallBack<T> getRequestCallBack() {
        return this.callback;
    }

    // 执行请求
    @SuppressWarnings("unchecked")
    private ResponseInfo<T> sendRequest(HttpURLConnection conn) throws HttpException{
        //todo add retry time
        while (true) {
            if (autoResume && isDownloadingFile) {
                File downloadFile = new File(fileSavePath);
                long fileLen = 0;
                if (downloadFile.isFile() && downloadFile.exists()) {
                    fileLen = downloadFile.length();
                }
                if (fileLen > 0) {
                    conn.setRequestProperty("RANGE","bytes=" + fileLen + "-");
                }
            }
            boolean retry = true;
            requestMethod = conn.getRequestMethod();
            //todo get String from cache
            ResponseInfo<T> responseInfo = null;
            if (!isCancelled()) {
                try {
                    responseInfo = handleResponse(conn);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new HttpException(e);
                }
            }
            return responseInfo;

        }
    }

    @Override
    protected Void doInBackground(Object... params) {
        if (this.state == State.CANCELLED || params == null || params.length == 0) return null;

        if (params.length > 3) {
            fileSavePath = String.valueOf(params[1]);
            isDownloadingFile = fileSavePath != null;
            autoResume = (Boolean) params[2];
            autoRename = (Boolean) params[3];
        }

        try {
            if (this.state == State.CANCELLED) return null;
            // init request & requestUrl
            conn = (HttpURLConnection) params[0];
            requestUrl = conn.getURL();
            if (callback != null) {
                callback.setRequestUrl(requestUrl.toString());
            }

            this.publishProgress(UPDATE_START);

            lastUpdateTime = SystemClock.uptimeMillis();

            ResponseInfo<T> responseInfo = sendRequest(conn);
            if (responseInfo != null) {
                this.publishProgress(UPDATE_SUCCESS, responseInfo);
                return null;
            }
        } catch (Exception e) {
            this.publishProgress(UPDATE_FAILURE, e, e.getMessage());
        }

        return null;
    }

    private final static int UPDATE_START = 1;
    private final static int UPDATE_LOADING = 2;
    private final static int UPDATE_FAILURE = 3;
    private final static int UPDATE_SUCCESS = 4;

    @Override
    @SuppressWarnings("unchecked")
    protected void onProgressUpdate(Object... values) {
        if (this.state == State.CANCELLED || values == null || values.length == 0 || callback == null) return;
        switch ((Integer) values[0]) {
            case UPDATE_START:
                this.state = State.STARTED;
                callback.onStart();
                break;
            case UPDATE_LOADING:
                if (values.length != 3) return;
                this.state = State.LOADING;
                callback.onLoading(
                        Long.valueOf(String.valueOf(values[1])),
                        Long.valueOf(String.valueOf(values[2])),
                        isUploading);
                break;
            case UPDATE_FAILURE:
                if (values.length != 3) return;
                this.state = State.FAILURE;
                callback.onFailure((HttpException) values[1], (String) values[2]);
                break;
            case UPDATE_SUCCESS:
                if (values.length != 2) return;
                this.state = State.SUCCESS;
                callback.onSuccess((ResponseInfo<T>) values[1]);
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseInfo<T> handleResponse(HttpURLConnection conn) throws HttpException, IOException {
        if (conn == null) {
            throw new HttpException("response is null");
        }
        if (isCancelled()) return null;

        int statusCode = 0;
        try {
            statusCode = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            throw new HttpException(e);
        }
        if (statusCode < 300) {
            Object result = null;
            isUploading = false;
            if (isDownloadingFile) {
                autoResume = autoResume && OtherUtils.isSupportRange(conn);
                String responseFileName = autoRename ? OtherUtils.getFileNameFromHttpResponse(conn) : null;
                FileDownloadHandler downloadHandler = new FileDownloadHandler();
                result = downloadHandler.handleEntity(conn, this, fileSavePath, autoResume, responseFileName);
            } else {
                //todo add StringRequest
//                StringDownloadHandler downloadHandler = new StringDownloadHandler();
//                result = downloadHandler.handleEntity(entity, this, charset);
//                if (HttpUtils.sHttpCache.isEnabled(requestMethod)) {
//                    HttpUtils.sHttpCache.put(requestUrl, (String) result, expiry);
//                }
            }
            return new ResponseInfo<T>(conn, (T) result, false);
        } else if (statusCode == 301 || statusCode == 302) {
            //todo add DirectHandler
//            if (httpRedirectHandler == null) {
//                httpRedirectHandler = new DefaultHttpRedirectHandler();
//            }
//            HttpRequestBase request = httpRedirectHandler.getDirectRequest(response);
//            if (request != null) {
//                return this.sendRequest(request);
//            }
        } else if (statusCode == 416) {
            throw new HttpException(statusCode, "maybe the file has downloaded completely");
        } else {
            throw new HttpException(statusCode, "other erro");
        }
        return null;
    }

    /**
     * cancel request task.
     */
    @Override
    public void cancel() {
        this.state = State.CANCELLED;

        if (callback != null) {
            callback.onCancelled();
        }

        if (conn != null) {
            try {
                long beforeTime = System.currentTimeMillis();
                //TODO 用线程关闭conn，否则在主线程会出现卡住的延迟情况，现在暂时用Conn=null来测试无延迟
//                conn.disconnect();
                conn = null;
                long afterTime = System.currentTimeMillis();
                Log.e("PriorityExecutor","--disconnect time="+(afterTime - beforeTime));
            } catch (Throwable e) {
            }
        }
        if (!this.isCancelled()) {
            try {
                this.cancel(true);
            } catch (Throwable e) {
            }
        }
    }

    private long lastUpdateTime;

    @Override
    public boolean updateProgress(long total, long current, boolean forceUpdateUI) {
        if (callback != null && this.state != State.CANCELLED) {
            if (forceUpdateUI) {
                this.publishProgress(UPDATE_LOADING, total, current);
            } else {
                long currTime = SystemClock.uptimeMillis();
                if (currTime - lastUpdateTime >= callback.getRate()) {
                    lastUpdateTime = currTime;
                    this.publishProgress(UPDATE_LOADING, total, current);
                }
            }
        }
        return this.state != State.CANCELLED;
    }

    public enum State {
        WAITING(0), STARTED(1), LOADING(2), FAILURE(3), CANCELLED(4), SUCCESS(5);
        private int value = 0;

        State(int value) {
            this.value = value;
        }

        public static State valueOf(int value) {
            switch (value) {
                case 0:
                    return WAITING;
                case 1:
                    return STARTED;
                case 2:
                    return LOADING;
                case 3:
                    return FAILURE;
                case 4:
                    return CANCELLED;
                case 5:
                    return SUCCESS;
                default:
                    return FAILURE;
            }
        }

        public int value() {
            return this.value;
        }
    }


}
