package cn.laclab.client.finaldownload.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.laclab.client.finaldownload.R;
import cn.laclab.client.finaldownload.download.DownloadInfo;
import cn.laclab.client.finaldownload.download.DownloadManager;
import cn.laclab.client.finaldownload.download.DownloadService;


/**
 * Created by sinye on 15/10/20.
 */
public class DownloadActivity extends AppCompatActivity implements View.OnClickListener{
    private Button downloadBtn;
    private Button listBtn;

    private DownloadManager downloadManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        downloadManager = DownloadService.getDownloadManager(getApplicationContext());

        downloadBtn = (Button) findViewById(R.id.download_btn);
        downloadBtn.setOnClickListener(this);
        listBtn = (Button) findViewById(R.id.download_page_btn);
        listBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.download_btn:
                download();
                break;
            case R.id.download_page_btn:
                list();
                break;
        }
    }

    public void download(){
        List<DownloadInfo> list = DataSource.getInstance().getData();
        if(list != null && list.size() > 0){
            for(DownloadInfo info : list){
                downloadManager.addNewDownload(info,null);
            }
        }
        Toast.makeText(this,"已开始下载，请在下载页面查看",Toast.LENGTH_SHORT).show();
    }

//    public void download(){
//        String target = "/sdcard/xUtils/" + System.currentTimeMillis() + "lzfile.jpg";
//        try {
//            downloadManager.addNewDownload("http://www.baidu.com",
//                    "yangmi",
//                    target,
//                    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
//                    false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
//                    null);
//        } catch (Exception e) {
//            LogUtils.e(e.getMessage(), e);
//        }
//    }

    public void list(){
        Intent intent = new Intent(this, DownloadListActivity.class);
        startActivity(intent);
    }
}
