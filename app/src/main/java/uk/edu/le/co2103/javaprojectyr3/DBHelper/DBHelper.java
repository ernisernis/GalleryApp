package uk.edu.le.co2103.javaprojectyr3.DBHelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static final int DATABASE_VER = 1;
    private static final String DATABASE_NAME = "GalleryApp3.db";

//    public static final String TABLE_NAME="CONTACTS";
//    public static final String TABLE_NAME2="IMAGE";
    public static final String TABLE_NAME3="IMAGEFILE";
    public static final String TABLE_NAME4="IMAGEFILE2";

//    public static final String COLUMN_EMAIL="EMAIL";
//    public static final String COLUMN_EMAIL2="IMAGE";
    public static final String COLUMN_EMAIL3="IMAGEFILE";
    public static final String COLUMN_EMAIL4="_id";
    public static final String COLUMN_EMAIL5="IMAGEFILE";

//    public static final String PASS_PHARSE = "!@#ABC"; //password encrypt

//    private static final String SQL_CREATE_TABLE_QUERY="CREATE TABLE "+TABLE_NAME+" ("+COLUMN_EMAIL+" TEXT PRIMARY KEY)";
//    private static final String SQL_CREATE_TABLE_QUERY2="CREATE TABLE "+TABLE_NAME2+" ("+COLUMN_EMAIL2+" TEXT PRIMARY KEY)";
    private static final String SQL_CREATE_TABLE_QUERY3="CREATE TABLE "+TABLE_NAME3+" ("+COLUMN_EMAIL3+" BLOB PRIMARY KEY)";
    private static final String SQL_CREATE_TABLE_QUERY4="CREATE TABLE "+TABLE_NAME4+" ("+COLUMN_EMAIL4+" INTEGER PRIMARY KEY, "+COLUMN_EMAIL5+" BLOB  )";
//                                                         CREATE TABLE " + DB_TABLE + "("+ KEY_NAME + " TEXT," + KEY_IMAGE + " BLOB);";

//    private static final String SQL_DELETE_TABLE_QUERY="DROP TABLE IF EXISTS " + TABLE_NAME;
//    private static final String SQL_DELETE_TABLE_QUERY2="DROP TABLE IF EXISTS " + TABLE_NAME2;
    private static final String SQL_DELETE_TABLE_QUERY3="DROP TABLE IF EXISTS " + TABLE_NAME3;
    private static final String SQL_DELETE_TABLE_QUERY4="DROP TABLE IF EXISTS " + TABLE_NAME4;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    static public synchronized DBHelper getInstance(Context context){
        if (instance==null) instance = new DBHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_QUERY);
//        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_QUERY2);
//        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_QUERY3);
//        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_QUERY4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_QUERY);
//        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_QUERY2);
//        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_QUERY3);
//        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_QUERY4);
        onCreate(sqLiteDatabase);
    }

    //CRUD Method
//    public void insertNewEmail (String email) {
//        SQLiteDatabase db = instance.getWritableDatabase(PASS_PHARSE);
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_EMAIL, email);
//        db.insert(TABLE_NAME,null,values);
//        db.close();
//    }
//    public void insertNewImage(String byteArray, String password) {
//        SQLiteDatabase db = instance.getWritableDatabase(password);
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_EMAIL2, byteArray);
//        db.insert(TABLE_NAME2,null,values);
//        db.close();
//    }
    public void insertNewImageBlob (byte[] image, String password) {
        SQLiteDatabase db = instance.getWritableDatabase(password);
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL5, image);
        db.insert(TABLE_NAME4,null,values);
        db.close();
    }
//    public void updateEmail (String oldEmail, String newEmail) {
//        SQLiteDatabase db = instance.getWritableDatabase(PASS_PHARSE);
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_EMAIL, newEmail);
//        db.update(TABLE_NAME,values, COLUMN_EMAIL+"='"+oldEmail+"'", null);
//        db.close();
//    }

//    public void deleteEmail (String email) {
//        SQLiteDatabase db = instance.getWritableDatabase(PASS_PHARSE);
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_EMAIL, email);
//        db.delete(TABLE_NAME,COLUMN_EMAIL+"='"+email+"'",null);
//        db.close();
//    }

    public void deleteImage (String password, byte[] image) {

        SQLiteDatabase db = instance.getWritableDatabase(password);
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_NAME4), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") byte[] imageByteArray = cursor.getBlob(cursor.getColumnIndex(COLUMN_EMAIL5));
                System.out.println(Arrays.toString(imageByteArray));
                if (Arrays.toString(imageByteArray).equals(Arrays.toString(image))) {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_EMAIL4));
                    db.delete(TABLE_NAME4, COLUMN_EMAIL4+"='"+ id +"'", null);
                    db.close();
                    cursor.close();
                    break;
                }
                cursor.moveToNext();
            }
        }
    }
//    public List<String> getAllEmail() {
//        SQLiteDatabase db = instance.getWritableDatabase(PASS_PHARSE);
//
//        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_NAME), null);
//        List<String> emails = new ArrayList<>();
//        if (cursor.moveToFirst()) {
//            while (!cursor.isAfterLast()) {
//                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
//                emails.add(email);
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//        db.close();
//
//        return emails;
//    }
//    public List<String> getAllImages(String password) {
//        SQLiteDatabase db = instance.getWritableDatabase(password);
//
//        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_NAME2), null);
//        List<String> images = new ArrayList<>();
//        if (cursor.moveToFirst()) {
//            while (!cursor.isAfterLast()) {
//                @SuppressLint("Range") String image = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL2));
//                images.add(image);
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//        db.close();
//
//        return images;
//    }

    public List<byte[]> getAllImagesByteArray(String password) {
        SQLiteDatabase db = instance.getWritableDatabase(password);
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_NAME4), null);
        List<byte[]> imagesByteArray = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") byte[] imageByteArray = cursor.getBlob(cursor.getColumnIndex(COLUMN_EMAIL5));
                imagesByteArray.add(imageByteArray);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return imagesByteArray;
    }

    public List<String> getAllFoldersStringArray(String password) {
        SQLiteDatabase db = instance.getWritableDatabase(password);
        Cursor cursor = db.rawQuery("SELECT name from sqlite_master where type='table'",null);
        List<String> foldersStringArray = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String folderString = cursor.getString(cursor.getColumnIndex("name"));
                foldersStringArray.add(folderString);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return foldersStringArray;
    }

    public void createFolder(String password, String folderName) {
//        private static final String SQL_CREATE_TABLE_QUERY4="CREATE TABLE "+TABLE_NAME4+" ("+COLUMN_EMAIL4+" INTEGER PRIMARY KEY, "+COLUMN_EMAIL5+" BLOB  )";
//        SQLiteDatabase.loadLibs();
        SQLiteDatabase db = instance.getWritableDatabase(password);
        String CREATE_NEW_FOLDER = "CREATE TABLE " + folderName + " (_id INTEGER PRIMARY KEY, ImageBlob BLOB )";
        db.execSQL(CREATE_NEW_FOLDER);
        db.close();
        System.out.println(password);
        System.out.println(folderName);
    }
}
