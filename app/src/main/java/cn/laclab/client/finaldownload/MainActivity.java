package cn.laclab.client.finaldownload;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cn.laclab.client.finaldownload.demo.DownloadActivity;
import cn.laclab.client.finaldownload.demo.db.DBActivity;
import cn.laclab.client.finaldownload.test.MyAsyncTaskActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mBtn0;
    private Button mBtn1;
    private Button mBtn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn0 = (Button) findViewById(R.id.btn0);
        mBtn0.setOnClickListener(this);
        mBtn1 = (Button) findViewById(R.id.btn1);
        mBtn1.setOnClickListener(this);
        mBtn2 = (Button) findViewById(R.id.btn2);
        mBtn2.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn0:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MyAsyncTaskActivity.class);
                startActivity(intent);
                break;
            case R.id.btn1:
                Intent donwloadIntent = new Intent();
                donwloadIntent.setClass(MainActivity.this, DownloadActivity.class);
                startActivity(donwloadIntent);
                break;
            case R.id.btn2:
                Intent dbIntent = new Intent();
                dbIntent.setClass(MainActivity.this, DBActivity.class);
                startActivity(dbIntent);
                break;
        }
    }
}
