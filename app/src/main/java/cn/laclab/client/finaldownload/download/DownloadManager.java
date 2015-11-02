package cn.laclab.client.finaldownload.download;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.laclab.client.finaldownload.core.HttpUtils;
import cn.laclab.client.finaldownload.core.callback.RequestCallBack;
import cn.laclab.client.finaldownload.core.exception.HttpException;
import cn.laclab.client.finaldownload.core.http.HttpHandler;
import cn.laclab.client.finaldownload.core.http.ResponseInfo;

public class DownloadManager {
    private final AtomicInteger mCount = new AtomicInteger(1);

    private List<DownloadInfo> downloadInfoList;

    private int maxDownloadThread = 3;

    private Context mContext;

    /*package*/ DownloadManager(Context appContext) {
        mContext = appContext;
        if (downloadInfoList == null) {
            downloadInfoList = new ArrayList<DownloadInfo>();
        }
    }

    public int getDownloadInfoListCount() {
        return downloadInfoList.size();
    }

    public DownloadInfo getDownloadInfo(int index) {
        return downloadInfoList.get(index);
    }

    public void addNewDownload(DownloadInfo info,final RequestCallBack<File> callback){
        info.setId(mCount.getAndIncrement());
        HttpUtils http = new HttpUtils();
        HttpHandler<File> handler = http.download(info, new ManagerCallBack(info, callback));
        info.setHandler(handler);
        info.setState(handler.getState());
        downloadInfoList.add(info);
    }

    public void addNewDownload(String url, String fileName, String target,
                               boolean autoResume, boolean autoRename,
                               final RequestCallBack<File> callback) throws Exception {
        final DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setDownloadUrl(url);
        downloadInfo.setAutoRename(autoRename);
        downloadInfo.setAutoResume(autoResume);
        downloadInfo.setFileName(fileName);
        downloadInfo.setFileSavePath(target);
        downloadInfo.setId(mCount.getAndIncrement());
        HttpUtils http = new HttpUtils();
//        http.configRequestThreadPoolSize(maxDownloadThread);
        HttpHandler<File> handler = http.download(url, target, autoResume, autoRename, new ManagerCallBack(downloadInfo, callback));
        downloadInfo.setHandler(handler);
        downloadInfo.setState(handler.getState());
        downloadInfoList.add(downloadInfo);
    }

    public void resumeDownload(int index, final RequestCallBack<File> callback) throws Exception {
        final DownloadInfo downloadInfo = downloadInfoList.get(index);
        resumeDownload(downloadInfo, callback);
    }

    public void resumeDownload(DownloadInfo downloadInfo, final RequestCallBack<File> callback) throws Exception {
        HttpUtils http = new HttpUtils();
//        http.configRequestThreadPoolSize(maxDownloadThread);
        HttpHandler<File> handler = http.download(
                downloadInfo.getDownloadUrl(),
                downloadInfo.getFileSavePath(),
                downloadInfo.isAutoResume(),
                downloadInfo.isAutoRename(),
                new ManagerCallBack(downloadInfo, callback));
        downloadInfo.setHandler(handler);
        downloadInfo.setState(handler.getState());
    }

    public void removeDownload(int index) throws Exception {
        DownloadInfo downloadInfo = downloadInfoList.get(index);
        removeDownload(downloadInfo);
    }

    public void removeDownload(DownloadInfo downloadInfo) throws Exception {
        HttpHandler<File> handler = downloadInfo.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        }
        downloadInfoList.remove(downloadInfo);
    }

    public void stopDownload(int index) throws Exception {
        DownloadInfo downloadInfo = downloadInfoList.get(index);
        stopDownload(downloadInfo);
    }

    public void stopDownload(DownloadInfo downloadInfo) throws Exception {
        HttpHandler<File> handler = downloadInfo.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        } else {
            downloadInfo.setState(HttpHandler.State.CANCELLED);
        }
    }

    public void stopAllDownload() throws Exception {
        for (DownloadInfo downloadInfo : downloadInfoList) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null && !handler.isCancelled()) {
                handler.cancel();
            } else {
                downloadInfo.setState(HttpHandler.State.CANCELLED);
            }
        }
    }

    public void backupDownloadInfoList() throws Exception {
        for (DownloadInfo downloadInfo : downloadInfoList) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
        }
    }

    public int getMaxDownloadThread() {
        return maxDownloadThread;
    }

    public void setMaxDownloadThread(int maxDownloadThread) {
        this.maxDownloadThread = maxDownloadThread;
    }

    public class ManagerCallBack extends RequestCallBack<File> {
        private DownloadInfo downloadInfo;
        private RequestCallBack<File> baseCallBack;

        public RequestCallBack<File> getBaseCallBack() {
            return baseCallBack;
        }

        public void setBaseCallBack(RequestCallBack<File> baseCallBack) {
            this.baseCallBack = baseCallBack;
        }

        private ManagerCallBack(DownloadInfo downloadInfo, RequestCallBack<File> baseCallBack) {
            this.baseCallBack = baseCallBack;
            this.downloadInfo = downloadInfo;
        }

        @Override
        public Object getUserTag() {
            if (baseCallBack == null) return null;
            return baseCallBack.getUserTag();
        }

        @Override
        public void setUserTag(Object userTag) {
            if (baseCallBack == null) return;
            baseCallBack.setUserTag(userTag);
        }

        @Override
        public void onStart() {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
//            try {
//                db.saveOrUpdate(downloadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onStart();
            }
        }

        @Override
        public void onCancelled() {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
//            try {
//                db.saveOrUpdate(downloadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onCancelled();
            }
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            downloadInfo.setFileLength(total);
            downloadInfo.setProgress(current);
//            try {
//                db.saveOrUpdate(downloadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onLoading(total, current, isUploading);
            }
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
//            try {
//                db.saveOrUpdate(downloadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onSuccess(responseInfo);
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
//            try {
//                db.saveOrUpdate(downloadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onFailure(error, msg);
            }
        }
    }

//    private class HttpHandlerStateConverter implements ColumnConverter<HttpHandler.State> {
//
//        @Override
//        public HttpHandler.State getFieldValue(Cursor cursor, int index) {
//            return HttpHandler.State.valueOf(cursor.getInt(index));
//        }
//
//        @Override
//        public HttpHandler.State getFieldValue(String fieldStringValue) {
//            if (fieldStringValue == null) return null;
//            return HttpHandler.State.valueOf(fieldStringValue);
//        }
//
//        @Override
//        public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
//            return fieldValue.value();
//        }
//
//        @Override
//        public ColumnDbType getColumnDbType() {
//            return ColumnDbType.INTEGER;
//        }
//    }
}
