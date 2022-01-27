package uk.edu.le.co2103.javaprojectyr3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;

public class ThirdActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 121;
    public static final int CAMERA_REQUEST_CODE = 131;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1; //for camera

    String currentPhotoPath;
    ImageView imageView, imageView2;
    Button goBack, tkPicture;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        SQLiteDatabase.loadLibs(this);
        goBack = findViewById(R.id.button);
        tkPicture = findViewById(R.id.btnTakePicture);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);

        ArrayList<String> imagesByteArray = new ArrayList<>(DBHelper.getInstance(this).getAllImages());
        recyclerView = findViewById(R.id.recyclerView);
        RVAdapter myAdapter = new RVAdapter(this,imagesByteArray);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        List<String> abc = new ArrayList<>(DBHelper.getInstance(this).getAllImages());
//        System.out.println(imagesByteArray.get(0));
//        String ddd = imagesByteArray.get(0).replace("[","");
//        ddd = ddd.replace("]","");
//        System.out.println(ddd);
//
//        String[] bytesString = ddd.split(", ");
//        byte[] bytes = new byte[bytesString.length];
//        for(int i = 0 ; i < bytes.length ; ++i) {
//            bytes[i] = Byte.parseByte(bytesString[i]);
//        }
//
//        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        ImageView image = (ImageView) findViewById(R.id.imageView2);
//        image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 800, 800, false));

//        reloadImages();


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThirdActivity.this,MainActivity.class);
                Log.d("A","Hello!");
                Log.d("A", DBHelper.getInstance(ThirdActivity.this).getAllImages().toString());
                startActivity(intent);
            }
        });

        tkPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
//                dispatchTakePictureIntent();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE) {
                File f = new File(currentPhotoPath);
                System.out.println(f);
                System.out.println(Arrays.toString(readFile(currentPhotoPath)));
                imageView.setImageURI(Uri.fromFile(f));
                DBHelper.getInstance(ThirdActivity.this).insertNewImage(Arrays.toString(readFile(currentPhotoPath)));
//                reloadImages();

        }
    }
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
//            openCamera();
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // open the camera;
                // openCamera();
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Need Camera Permission!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private File createImageFile() throws IOException {
        //creating timestamp of the file
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // erorr
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
    }
    private byte[] readFile(String file) {
        ByteArrayOutputStream bos = null;
        try {
            File f = new File(file);
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1;) {
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }
}
