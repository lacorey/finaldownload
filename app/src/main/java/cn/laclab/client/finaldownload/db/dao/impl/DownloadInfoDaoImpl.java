package cn.laclab.client.finaldownload.db.dao.impl;

import android.content.Context;

import cn.laclab.client.database.dao.impl.BaseDaoImpl;
import cn.laclab.client.database.demo.util.DBHelper;
import cn.laclab.client.finaldownload.download.DownloadInfo;

/**
 * Created by sinye on 15/11/3.
 */
public class DownloadInfoDaoImpl extends BaseDaoImpl<DownloadInfo> {
    public DownloadInfoDaoImpl(Context context) {
        super(new DBHelper(context),DownloadInfo.class);
    }
}
