package cn.laclab.client.finaldownload.demo.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.laclab.client.finaldownload.R;
import cn.laclab.client.finaldownload.download.DownloadInfo;

/**
 * Created by sinye on 15/11/3.
 */
public class DBAdapter extends BaseAdapter{
    private List<DownloadInfo> list;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public DBAdapter(Context context,List<DownloadInfo> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.db_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DownloadInfo info = list.get(i);
        if(info != null){
            Picasso.with(mContext).load(info.getIcon()).into(holder.imageView);
            holder.name.setText(info.getFileName());
        }
        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        TextView name;
    }
}
