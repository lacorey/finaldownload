package cn.laclab.client.database.demo.util;


import android.content.Context;

import cn.laclab.client.database.demo.model.Student;
import cn.laclab.client.database.demo.model.Teacher;
import cn.laclab.client.database.util.MyDBHelper;

public class DBHelper extends MyDBHelper {
	private static final String DBNAME = "school.db";
	private static final int DBVERSION = 1;
	private static final Class<?>[] clazz = { Teacher.class, Student.class };//

	public DBHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}

}
