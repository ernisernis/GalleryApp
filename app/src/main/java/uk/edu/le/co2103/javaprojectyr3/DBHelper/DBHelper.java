package uk.edu.le.co2103.javaprojectyr3.DBHelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static final int DATABASE_VER = 1;
    private static final String DATABASE_NAME = "GalleryApp3.db"; //Added 2 to the name

    public static final String TABLE_NAME="CONTACTS";
    public static final String TABLE_NAME2="IMAGE";

    public static final String COLUMN_EMAIL="EMAIL";
    public static final String COLUMN_EMAIL2="IMAGE";

    public static final String PASS_PHARSE = "!@#ABC"; //password encrypt

    private static final String SQL_CREATE_TABLE_QUERY="CREATE TABLE "+TABLE_NAME+" ("+COLUMN_EMAIL+" TEXT PRIMARY KEY)";
    private static final String SQL_CREATE_TABLE_QUERY2="CREATE TABLE "+TABLE_NAME2+" ("+COLUMN_EMAIL2+" TEXT PRIMARY KEY)";

    private static final String SQL_DELETE_TABLE_QUERY="DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String SQL_DELETE_TABLE_QUERY2="DROP TABLE IF EXISTS " + TABLE_NAME2;

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
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_QUERY2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_QUERY);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_QUERY2);
        onCreate(sqLiteDatabase);
    }

    //CRUD Method
    public void insertNewEmail (String email) {
        SQLiteDatabase db = instance.getWritableDatabase(PASS_PHARSE);
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        db.insert(TABLE_NAME,null,values);
        db.close();
    }
    public void insertNewImage(String byteArray) {
        SQLiteDatabase db = instance.getWritableDatabase(PASS_PHARSE);
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL2, byteArray);
        db.insert(TABLE_NAME2,null,values);
        db.close();
    }
    public void updateEmail (String oldEmail, String newEmail) {
        SQLiteDatabase db = instance.getWritableDatabase(PASS_PHARSE);
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, newEmail);
        db.update(TABLE_NAME,values, COLUMN_EMAIL+"='"+oldEmail+"'", null);
        db.close();
    }

    public void deleteEmail (String email) {
        SQLiteDatabase db = instance.getWritableDatabase(PASS_PHARSE);
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        db.delete(TABLE_NAME,COLUMN_EMAIL+"='"+email+"'",null);
        db.close();
    }
    public List<String> getAllEmail() {
        SQLiteDatabase db = instance.getWritableDatabase(PASS_PHARSE);

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_NAME), null);
        List<String> emails = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
                emails.add(email);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return emails;
    }
}
