package cn.laclab.client.database.demo.util;


import android.content.Context;

import cn.laclab.client.database.demo.model.Student;
import cn.laclab.client.database.demo.model.Teacher;
import cn.laclab.client.database.util.MyDBHelper;
import cn.laclab.client.finaldownload.download.DownloadInfo;

public class DBHelper extends MyDBHelper {
	private static final String DBNAME = "finaldownload.db";
	private static final int DBVERSION = 1;
	private static final Class<?>[] clazz = { DownloadInfo.class};//

	public DBHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}

}
