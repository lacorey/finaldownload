package cn.laclab.client.finaldownload.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;

import cn.laclab.client.finaldownload.R;
import cn.laclab.client.finaldownload.core.callback.RequestCallBack;
import cn.laclab.client.finaldownload.core.exception.HttpException;
import cn.laclab.client.finaldownload.core.http.HttpHandler;
import cn.laclab.client.finaldownload.core.http.ResponseInfo;
import cn.laclab.client.finaldownload.core.util.LogUtils;
import cn.laclab.client.finaldownload.download.DownloadInfo;
import cn.laclab.client.finaldownload.download.DownloadManager;
import cn.laclab.client.finaldownload.download.DownloadService;

public class DownloadListActivity extends AppCompatActivity {

    private ListView downloadList;

    private DownloadManager downloadManager;
    private DownloadListAdapter downloadListAdapter;

    private Context mAppContext;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_list);

        mAppContext = this.getApplicationContext();

        downloadManager = DownloadService.getDownloadManager(mAppContext);

        downloadList = (ListView) findViewById(R.id.download_list);
        downloadListAdapter = new DownloadListAdapter(mAppContext);
        downloadList.setAdapter(downloadListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        downloadListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        try {
            if (downloadListAdapter != null && downloadManager != null) {
                downloadManager.backupDownloadInfoList();
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }
        super.onDestroy();
    }

    private class DownloadListAdapter extends BaseAdapter {

        private final Context mContext;
        private final LayoutInflater mInflater;

        private DownloadListAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (downloadManager == null) return 0;
            return downloadManager.getDownloadInfoListCount();
        }

        @Override
        public Object getItem(int i) {
            return downloadManager.getDownloadInfo(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressWarnings("unchecked")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            DownloadItemViewHolder holder = null;
            DownloadInfo downloadInfo = downloadManager.getDownloadInfo(i);
            if (view == null) {
                view = mInflater.inflate(R.layout.download_item, null);
                holder = new DownloadItemViewHolder(downloadInfo);
                holder.label = (TextView) view.findViewById(R.id.download_label);
                holder.state = (TextView) view.findViewById(R.id.download_state);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.download_pb);
                holder.stopBtn = (Button) view.findViewById(R.id.download_stop_btn);
                holder.removeBtn = (Button) view.findViewById(R.id.download_remove_btn);
                holder.icon = (ImageView) view.findViewById(R.id.icon);
                view.setTag(holder);
                holder.refresh();
            } else {
                holder = (DownloadItemViewHolder) view.getTag();
                holder.update(downloadInfo);
            }

            final DownloadItemViewHolder finalHolder = holder;
            holder.stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalHolder.stop(v);
                }
            });

            holder.removeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    finalHolder.remove(v);
                }
            });

            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                RequestCallBack callBack = handler.getRequestCallBack();
                if (callBack instanceof DownloadManager.ManagerCallBack) {
                    DownloadManager.ManagerCallBack managerCallBack = (DownloadManager.ManagerCallBack) callBack;
                    if (managerCallBack.getBaseCallBack() == null) {
                        managerCallBack.setBaseCallBack(new DownloadRequestCallBack());
                    }
                }
                callBack.setUserTag(new WeakReference<DownloadItemViewHolder>(holder));
            }

            return view;
        }
    }

    public class DownloadItemViewHolder {
        TextView label;
        TextView state;
        ProgressBar progressBar;
        Button stopBtn;
        Button removeBtn;
        ImageView icon;

        private DownloadInfo downloadInfo;

        public DownloadItemViewHolder(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        public void stop(View view) {
            HttpHandler.State state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                case LOADING:
                    try {
                        downloadManager.stopDownload(downloadInfo);
                    } catch (Exception e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                    break;
                case CANCELLED:
                case FAILURE:
                    try {
                        downloadManager.resumeDownload(downloadInfo, new DownloadRequestCallBack());
                    } catch (Exception e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                    downloadListAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

        public void remove(View view) {
            try {
                downloadManager.removeDownload(downloadInfo);
                downloadListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                LogUtils.e(e.getMessage(), e);
            }
        }

        public void update(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
            refresh();
        }

        public void refresh() {
            label.setText(downloadInfo.getFileName());
            state.setText(downloadInfo.getState().toString());
            if (downloadInfo.getFileLength() > 0) {
                progressBar.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
            } else {
                progressBar.setProgress(0);
            }
            Picasso.with(mAppContext).load(downloadInfo.getIcon()).into(icon);

            stopBtn.setVisibility(View.VISIBLE);
            stopBtn.setText(mAppContext.getString(R.string.stop));
            HttpHandler.State state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                    stopBtn.setText(mAppContext.getString(R.string.stop));
                    break;
                case STARTED:
                    stopBtn.setText(mAppContext.getString(R.string.stop));
                    break;
                case LOADING:
                    stopBtn.setText(mAppContext.getString(R.string.stop));
                    break;
                case CANCELLED:
                    stopBtn.setText(mAppContext.getString(R.string.resume));
                    break;
                case SUCCESS:
                    stopBtn.setVisibility(View.INVISIBLE);
                    break;
                case FAILURE:
                    stopBtn.setText(mAppContext.getString(R.string.retry));
                    break;
                default:
                    break;
            }
        }
    }

    private class DownloadRequestCallBack extends RequestCallBack<File> {

        @SuppressWarnings("unchecked")
        private void refreshListItem() {
            if (userTag == null) return;
            WeakReference<DownloadItemViewHolder> tag = (WeakReference<DownloadItemViewHolder>) userTag;
            DownloadItemViewHolder holder = tag.get();
            if (holder != null) {
                holder.refresh();
            }
        }

        @Override
        public void onStart() {
            refreshListItem();
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            refreshListItem();
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            refreshListItem();
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            refreshListItem();
        }

        @Override
        public void onCancelled() {
            refreshListItem();
        }
    }
}