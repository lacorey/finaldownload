package cn.laclab.client.finaldownload;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;

import cn.laclab.client.finaldownload.core.http.HttpHandler;
import cn.laclab.client.finaldownload.db.dao.impl.DownloadInfoDaoImpl;
import cn.laclab.client.finaldownload.download.DownloadInfo;

/**
 * Created by sinye on 15/11/5.
 */
public class DBTest extends AndroidTestCase{

    public void testDB(){
        DownloadInfoDaoImpl infoImpl = new DownloadInfoDaoImpl(getContext());

        DownloadInfo appInfo = new DownloadInfo();
        appInfo.setId(10);
        appInfo.setDownloadUrl("www.google.com");
        appInfo.setFileName("google");
        appInfo.setFileSavePath("sdcard");
        appInfo.setIcon("aa.jpg");
        appInfo.setState(HttpHandler.State.LOADING);
        appInfo.setAutoResume(false);
        appInfo.setAutoRename(false);

//        infoImpl.saveOrUpdate(appInfo,"downloadUrl=?",new String[]{appInfo.getDownloadUrl()});

//        infoImpl.update(appInfo);


        long size= infoImpl.insert(appInfo,false);
        List<DownloadInfo> lists = infoImpl.find();
        for(int i=0;i<lists.size();i++){
            DownloadInfo downloadInfo = lists.get(i);
            Log.e("TAG","--before info.autoResume="+downloadInfo.isAutoResume());
        }

        appInfo.setAutoResume(true);
        infoImpl.update(appInfo);

        List<DownloadInfo> list = infoImpl.find();
        for(int i=0;i<list.size();i++){
            DownloadInfo downloadInfo = list.get(i);
            Log.e("TAG","--after info.autoResume="+downloadInfo.isAutoResume());
        }
    }
}
