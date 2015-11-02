package cn.laclab.client.finaldownload.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.laclab.client.finaldownload.R;
import cn.laclab.client.finaldownload.core.task.PriorityAsyncTask;

/**
 * Created by sinye on 15/10/27.
 */
public class MyAsyncTaskActivity extends Activity implements View.OnClickListener{
    private TextView textView;
    private TextView percentView;
    private Button beginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_asynctask);

        textView = (TextView)findViewById(R.id.title);
        percentView = (TextView) findViewById(R.id.percent);
        beginBtn = (Button) findViewById(R.id.begin);
        beginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.begin:
                MyAsyncTask task = new MyAsyncTask();
                task.execute("hello");
                break;
        }
    }

    private class MyAsyncTask extends PriorityAsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {
            textView.setText("loading...");
        }

        @Override
        protected String doInBackground(String... params) {
            //为了演示进度,休眠500毫秒
            try {
                for(int m=0;m<10;m++){
                    publishProgress(m);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "ok";
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            percentView.setText(progresses[0] + "");
            textView.setText("loading..." + progresses[0] + "%");
        }

        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }
    }
}
