package uk.edu.le.co2103.javaprojectyr3.DBHelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.CursorIndexOutOfBoundsException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static final int DATABASE_VER = 1;
    private static final String DATABASE_NAME = "GalleryApp3.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    static public synchronized DBHelper getInstance(Context context){
        if (instance==null) instance = new DBHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }

    public void insertNewImageBlob (byte[] image, String password, String folderName) {
        SQLiteDatabase db = instance.getWritableDatabase(password);
        ContentValues values = new ContentValues();
        values.put("ImageBlob", image);
        db.insert("'" + folderName + "'",null,values);
        db.close();
    }

    public void deleteImage (String password, byte[] image, String folderName) {

        SQLiteDatabase db = instance.getWritableDatabase(password);
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", folderName), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") byte[] imageByteArray = cursor.getBlob(cursor.getColumnIndex("ImageBlob"));
                if (Arrays.toString(imageByteArray).equals(Arrays.toString(image))) {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    db.delete("'" + folderName + "'", "_id"+"='"+ id +"'", null);
                    db.close();
                    cursor.close();
                    break;
                }
                cursor.moveToNext();
            }
        }
    }
    public void deleteFolder (String password, String folderName) {
        SQLiteDatabase db = instance.getWritableDatabase(password);
        db.execSQL("DROP TABLE IF EXISTS '" + folderName + "'");
        db.close();
    }

    public List<byte[]> getAllImagesByteArray(String password, String folderName) {
        SQLiteDatabase db = instance.getWritableDatabase(password);
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", folderName), null);
        List<byte[]> imagesByteArray = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") byte[] imageByteArray = cursor.getBlob(cursor.getColumnIndex("ImageBlob"));
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

    public int getFolderImageCount(String password, String folderName) {
        SQLiteDatabase db = instance.getWritableDatabase(password);
        Cursor cursor = db.rawQuery("SELECT COUNT(*) as count FROM '" + folderName + "'", null);
        cursor.moveToFirst();
        @SuppressLint("Range") int folderCount = cursor.getInt(cursor.getColumnIndex("count"));
        return folderCount;
    }

    @SuppressLint("Range")
    public byte[] getFirstFolderImage(String password, String folderName) {
        SQLiteDatabase db = instance.getWritableDatabase(password);
        Cursor cursor = db.rawQuery("SELECT ImageBlob FROM '" + folderName + "'",null);
        byte[] firstImageByte;
        try {
            cursor.moveToFirst();
            firstImageByte = cursor.getBlob(cursor.getColumnIndex("ImageBlob"));
        } catch (CursorIndexOutOfBoundsException e) {
            return null;
        }
        return firstImageByte;
    }

    public void createFolder(String password, String folderName) {
        SQLiteDatabase db = instance.getWritableDatabase(password);
        String CREATE_NEW_FOLDER = "CREATE TABLE '" + folderName + "' (_id INTEGER PRIMARY KEY, ImageBlob BLOB )";
        db.execSQL(CREATE_NEW_FOLDER);
        db.close();
    }
}
