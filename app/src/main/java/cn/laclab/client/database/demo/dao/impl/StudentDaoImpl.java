package cn.laclab.client.database.demo.dao.impl;

import android.content.Context;

import cn.laclab.client.database.dao.impl.BaseDaoImpl;
import cn.laclab.client.database.demo.model.Student;
import cn.laclab.client.database.demo.util.DBHelper;


//如果您是J2EE高手一定希望支持接口吧,按下面的写法即可:
//写一个接口:public interface StudentDao extends BaseDao<Student> {}
//实现接口: public class StudentDaoImpl extends BaseDaoImpl<Student> implements StudentDao
public class StudentDaoImpl extends BaseDaoImpl<Student> {
	public StudentDaoImpl(Context context) {
		super(new DBHelper(context),Student.class);
	}
}
