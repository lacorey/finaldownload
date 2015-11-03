package cn.laclab.client.finaldownload.demo.db;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import cn.laclab.client.finaldownload.R;
import cn.laclab.client.finaldownload.db.dao.impl.DownloadInfoDaoImpl;
import cn.laclab.client.finaldownload.demo.DataSource;
import cn.laclab.client.finaldownload.download.DownloadInfo;

/**
 * Created by sinye on 15/11/3.
 */
public class DBActivity extends AppCompatActivity implements View.OnClickListener{
    private Button insertBtn;
    private Button listBtn;
    private ListView mListView;
    private DBAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        getViews();
    }

    public void getViews(){
        insertBtn = (Button) findViewById(R.id.insert);
        insertBtn.setOnClickListener(this);
        listBtn = (Button) findViewById(R.id.list);
        listBtn.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.db_list);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.insert:
                insert();
                break;
            case R.id.list:
                list();
                break;
        }
    }

    /**
     * insert db
     */
    public void insert(){
        List<DownloadInfo> list = DataSource.getInstance().getData();
        DownloadInfoDaoImpl impl = new DownloadInfoDaoImpl(this);
        for(DownloadInfo info : list){
            if(impl.isExist(null,"downloadUrl=?",new String[]{info.getDownloadUrl()})){
                impl.update(info);
            }else{
                impl.insert(info);
            }
        }
    }

    public void list(){
        DownloadInfoDaoImpl impl = new DownloadInfoDaoImpl(this);
        List<DownloadInfo> infoList = impl.find();
        if(infoList != null && infoList.size() > 0){
            mAdapter = new DBAdapter(DBActivity.this,infoList);
            mListView.setAdapter(mAdapter);
        }
    }
}
