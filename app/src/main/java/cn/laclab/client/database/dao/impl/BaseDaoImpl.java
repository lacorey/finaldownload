package cn.laclab.client.database.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.laclab.client.database.annotation.Column;
import cn.laclab.client.database.annotation.Id;
import cn.laclab.client.database.annotation.Table;
import cn.laclab.client.database.dao.BaseDao;
import cn.laclab.client.database.util.TableHelper;


/**
 * Created by sinye on 15/10/20.
 */
public class BaseDaoImpl<T> implements BaseDao<T> {
    private String TAG = "ORM";
    private SQLiteOpenHelper dbHelper;
    private String tableName;
    private String idColumn;
    private Class<T> clazz;
    private List<Field> allFields;
    private static final int METHOD_INSERT = 0;
    private static final int METHOD_UPDATE = 1;

    private static final int TYPE_NOT_INCREMENT = 0;
    private static final int TYPE_INCREMENT = 1;

    public BaseDaoImpl(SQLiteOpenHelper dbHelper,Class<T> clazz){
        this.dbHelper = dbHelper;
        if(clazz == null){
            this.clazz = ((Class<T>)((ParameterizedType)super.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        }else{
            this.clazz = clazz;
        }

        if(this.clazz.isAnnotationPresent(Table.class)){
            Table table = this.clazz.getAnnotation(Table.class);
            this.tableName = table.name();
        }

        //加载所有字段
        this.allFields = TableHelper.joinFields(this.clazz.getDeclaredFields(), this.clazz.getSuperclass().getDeclaredFields());
        //找到主键
        for(Field field : this.allFields){
            if(field.isAnnotationPresent(Id.class)){
                Column column = field.getAnnotation(Column.class);
                this.idColumn = column.name();
                break;
            }
        }

        Log.d(TAG, "clazz:" + this.clazz + " tableName:" + this.tableName
                + " idColumn:" + this.idColumn);
    }

    public BaseDaoImpl(SQLiteOpenHelper dbHelper) {
        this(dbHelper, null);
    }

    @Override
    public SQLiteOpenHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    public long insert(T entity) {
        return insert(entity,true);
    }

    @Override
    public long insert(T entity, boolean flag) {
        String sql = "";
        SQLiteDatabase db = null;
        try {
            db = this.dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            if (flag) {
                sql = setContentValues(entity, cv, TYPE_INCREMENT,
                        METHOD_INSERT);// id自增
            } else {
                sql = setContentValues(entity, cv, TYPE_NOT_INCREMENT,
                        METHOD_INSERT);// id需指定
            }
            Log.d(TAG, "[insert]: insert into " + this.tableName + " " + sql);
            long row = db.insert(this.tableName, null, cv);
            return row;
        } catch (Exception e) {
            Log.d(this.TAG, "[insert] into DB Exception.");
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return 0L;
    }

    @Override
    public void delete(int id) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String where = this.idColumn + " = ?";
        String[] whereValue = { Integer.toString(id) };

        Log.d(TAG, "[delete]: delelte from " + this.tableName + " where "
                + where.replace("?", String.valueOf(id)));

        db.delete(this.tableName, where, whereValue);
        db.close();
    }

    @Override
    public void delete(Integer... ids) {
        if (ids.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < ids.length; i++) {
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            SQLiteDatabase db = this.dbHelper.getWritableDatabase();
            String sql = "delete from " + this.tableName + " where "
                    + this.idColumn + " in (" + sb + ")";

            Log.d(TAG, "[delete]: " + getLogSql(sql, ids));

            db.execSQL(sql, (Object[]) ids);
            db.close();
        }
    }

    @Override
    public void update(T entity) {
        SQLiteDatabase db = null;
        try {
            db = this.dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();

            String sql = setContentValues(entity, cv, TYPE_NOT_INCREMENT,
                    METHOD_UPDATE);

            String where = this.idColumn + " = ?";
            int id = Integer.parseInt(cv.get(this.idColumn).toString());
            cv.remove(this.idColumn);

            Log.d(TAG, "[update]: update " + this.tableName + " set " + sql
                    + " where " + where.replace("?", String.valueOf(id)));

            String[] whereValue = { Integer.toString(id) };
            db.update(this.tableName, cv, where, whereValue);
        } catch (Exception e) {
            Log.d(this.TAG, "[update] DB Exception.");
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
    }

    @Override
    public T get(int id) {
        String selection = this.idColumn+" = ?";
        String[] selectionArgs = {Integer.toString(id)};
        Log.d(TAG, "[get]: select * from " + this.tableName + " where "
                + this.idColumn + " = '" + id + "'");
        List<T> list = find(null,selection,selectionArgs,null,null,null,null);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<T> rawQuery(String sql, String[] selectionArgs) {
        Log.d(TAG, "[rawQuery]: " + getLogSql(sql, selectionArgs));
        List<T> list = new ArrayList<T>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = this.dbHelper.getReadableDatabase();
            cursor = db.rawQuery(sql,selectionArgs);
            getListFromCursor(list,cursor);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
            if(db != null){
                db.close();
            }
        }
        return list;
    }

    @Override
    public List<T> find() {
        return find(null,null,null,null,null,null,null);
    }

    @Override
    public List<T> find(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        Log.d(TAG, "[find]");
        List<T> list = new ArrayList<T>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = this.dbHelper.getReadableDatabase();
            cursor = db.query(this.tableName,columns,selection,selectionArgs,groupBy,having,orderBy,limit);
            Log.e("ORM","--cursor="+cursor);
            getListFromCursor(list, cursor);
        }catch (Exception e){
            Log.e("ORM","BaseDaoImpl find is error");
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;
    }

    @Override
    public boolean isExist(String sql, String[] selectionArgs) {
        Log.d(TAG, "[isExist]: " + getLogSql(sql, selectionArgs));

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = this.dbHelper.getReadableDatabase();
            cursor = db.rawQuery(sql,selectionArgs);
            if(cursor.getCount() > 0){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    @Override
    public List<Map<String, String>> query2MapList(String sql, String[] selectionArgs) {
        Log.d(TAG, "[query2MapList]: " + getLogSql(sql, selectionArgs));
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
        try {
            db = this.dbHelper.getReadableDatabase();
            cursor = db.rawQuery(sql, selectionArgs);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (String columnName : cursor.getColumnNames()) {
                    int c = cursor.getColumnIndex(columnName);
                    if (c < 0) {
                        continue; // 如果不存在循环下个属性值
                    } else {
                        map.put(columnName.toLowerCase(), cursor.getString(c));
                    }
                }
                retList.add(map);
            }
        } catch (Exception e) {
            Log.e(TAG, "[query2MapList] from DB exception");
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return retList;
    }

    @Override
    public void execSql(String sql, Object[] selectionArgs) {
        SQLiteDatabase db = null;
        Log.d(TAG, "[execSql]: " + getLogSql(sql, selectionArgs));
        try {
            db = this.dbHelper.getWritableDatabase();
            if (selectionArgs == null) {
                db.execSQL(sql);
            } else {
                db.execSQL(sql, selectionArgs);
            }
        } catch (Exception e) {
            Log.e(TAG, "[execSql] DB exception.");
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private String getLogSql(String sql, Object[] args) {
        if (args == null || args.length == 0) {
            return sql;
        }
        for (int i = 0; i < args.length; i++) {
            sql = sql.replaceFirst("\\?", "'" + String.valueOf(args[i]) + "'");
        }
        return sql;
    }

    private void getListFromCursor(List<T> list, Cursor cursor)
            throws IllegalAccessException, InstantiationException {
        while (cursor.moveToNext()) {
            T entity = this.clazz.newInstance();

            for (Field field : this.allFields) {
                Column column = null;
                if (field.isAnnotationPresent(Column.class)) {
                    column = (Column) field.getAnnotation(Column.class);

                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();

                    int c = cursor.getColumnIndex(column.name());
                    if (c < 0) {
                        continue; // 如果不存则循环下个属性值
                    } else if ((Integer.TYPE == fieldType)
                            || (Integer.class == fieldType)) {
                        field.set(entity, cursor.getInt(c));
                    } else if (String.class == fieldType) {
                        field.set(entity, cursor.getString(c));
                    } else if ((Long.TYPE == fieldType)
                            || (Long.class == fieldType)) {
                        field.set(entity, Long.valueOf(cursor.getLong(c)));
                    } else if ((Float.TYPE == fieldType)
                            || (Float.class == fieldType)) {
                        field.set(entity, Float.valueOf(cursor.getFloat(c)));
                    } else if ((Short.TYPE == fieldType)
                            || (Short.class == fieldType)) {
                        field.set(entity, Short.valueOf(cursor.getShort(c)));
                    } else if ((Double.TYPE == fieldType)
                            || (Double.class == fieldType)) {
                        field.set(entity, Double.valueOf(cursor.getDouble(c)));
                    } else if (Date.class == fieldType) {// 处理java.util.Date类型,update2012-06-10
                        Date date = new Date();
                        date.setTime(cursor.getLong(c));
                        field.set(entity, date);
                    } else if (Blob.class == fieldType) {
                        field.set(entity, cursor.getBlob(c));
                    } else if (Character.TYPE == fieldType) {
                        String fieldValue = cursor.getString(c);
                        if ((fieldValue != null) && (fieldValue.length() > 0)) {
                            field.set(entity, Character.valueOf(fieldValue
                                    .charAt(0)));
                        }
                    }
                }
            }
            list.add(entity);
        }
    }

    private String setContentValues(T entity, ContentValues cv, int type,
                                    int method) throws IllegalAccessException {
        StringBuffer strField = new StringBuffer("(");
        StringBuffer strValue = new StringBuffer(" values(");
        StringBuffer strUpdate = new StringBuffer(" ");
        for (Field field : this.allFields) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }
            Column column = (Column) field.getAnnotation(Column.class);

            field.setAccessible(true);
            Object fieldValue = field.get(entity);
            if (fieldValue == null)
                continue;
            if ((type == TYPE_INCREMENT)
                    && (field.isAnnotationPresent(Id.class))) {
                continue;
            }
            if (Date.class == field.getType()) {// 处理java.util.Date类型,update
                // 2012-06-10
                cv.put(column.name(), ((Date) fieldValue).getTime());
                continue;
            }
            String value = String.valueOf(fieldValue);
            cv.put(column.name(), value);
            if (method == METHOD_INSERT) {
                strField.append(column.name()).append(",");
                strValue.append("'").append(value).append("',");
            } else {
                strUpdate.append(column.name()).append("=").append("'").append(
                        value).append("',");
            }

        }
        if (method == METHOD_INSERT) {
            strField.deleteCharAt(strField.length() - 1).append(")");
            strValue.deleteCharAt(strValue.length() - 1).append(")");
            return strField.toString() + strValue.toString();
        } else {
            return strUpdate.deleteCharAt(strUpdate.length() - 1).append(" ")
                    .toString();
        }
    }






}
