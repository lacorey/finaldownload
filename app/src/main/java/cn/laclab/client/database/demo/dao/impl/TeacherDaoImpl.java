package cn.laclab.client.database.demo.dao.impl;


import android.content.Context;

import cn.laclab.client.database.dao.impl.BaseDaoImpl;
import cn.laclab.client.database.demo.model.Teacher;
import cn.laclab.client.database.demo.util.DBHelper;


public class TeacherDaoImpl extends BaseDaoImpl<Teacher> {
	public TeacherDaoImpl(Context context) {
		super(new DBHelper(context),Teacher.class);
	}
}
