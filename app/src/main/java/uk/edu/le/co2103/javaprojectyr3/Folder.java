package uk.edu.le.co2103.javaprojectyr3;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Folder {
    String title;
   ArrayList<Bitmap> bitmapArrayList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Bitmap> getBitmapArrayList() {
        return bitmapArrayList;
    }

    public void setBitmapArrayList(ArrayList<Bitmap> bitmapArrayList) {
        this.bitmapArrayList = bitmapArrayList;
    }
}
