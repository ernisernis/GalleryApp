package uk.edu.le.co2103.javaprojectyr3.DBHelper;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static final int DATABASE_VER = 1;
    private static final String DATABASE_NAME = "GalleryApp.db";

    public static final String TABLE_NAME="CONTACTS";
    public static final String COLUMN_EMAIL="EMAIL";

    public static final String PASS_PHARSE = "!@#ABC"; //password encrypt

    private static final String SQL_CREATE_TABLE_QUERY="CREATE TABLE "+TABLE_NAME+" ("+COLUMN_EMAIL+" TEXT PRIMARY KEY)";

    private static final String SQL_DELETE_TABLE_QUERY="DROP TABLE IF EXISTS " + TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    static public synchronized DBHelper getInstance(Context context){
        if (instance==null) instance = new DBHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_QUERY);
        onCreate(sqLiteDatabase);
    }
}
