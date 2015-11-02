package cn.laclab.client.database.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sinye on 15/10/20.
 */
public class MyDBHelper extends SQLiteOpenHelper{
    private Class<?>[] modelClasses;

    public MyDBHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion,Class<?>[] modelClasses) {
        super(context, databaseName, factory, databaseVersion);
        this.modelClasses = modelClasses;
    }

    public void onCreate(SQLiteDatabase db) {
        TableHelper.createTablesByClasses(db, this.modelClasses);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TableHelper.dropTablesByClasses(db, this.modelClasses);
        onCreate(db);
    }
}
