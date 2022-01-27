package uk.edu.le.co2103.javaprojectyr3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;

public class ThirdActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 121; //These numbers does not really matter, as long as they are different it's good.
    public static final int CAMERA_REQUEST_CODE = 131; //These numbers does not really matter, as long as they are different it's good.

    String currentPhotoPath;
    Button goBack, tkPicture;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        SQLiteDatabase.loadLibs(this);
        goBack = findViewById(R.id.button);
        tkPicture = findViewById(R.id.btnTakePicture);

        ArrayList<String> imagesByteArray = new ArrayList<>(DBHelper.getInstance(this).getAllImages());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setHasFixedSize(true);
        RVAdapter myAdapter = new RVAdapter(this,imagesByteArray);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        goBack.setOnClickListener(view -> {
            Intent intent = new Intent(ThirdActivity.this,MainActivity.class);
            startActivity(intent);
        });

        tkPicture.setOnClickListener(view -> askCameraPermissions());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE) {
                DBHelper.getInstance(ThirdActivity.this).insertNewImage(Arrays.toString(readFile(currentPhotoPath)));
                reloadImages();
        }
    }
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Need Camera Permission!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".png",
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
                Toast.makeText(ThirdActivity.this, "Error processing image file! : " + ex, Toast.LENGTH_LONG).show();
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
            Toast.makeText(ThirdActivity.this, "Error reading a photo! : " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e2) {
            Toast.makeText(ThirdActivity.this, "Error reading a photo! : " + e2.getMessage(), Toast.LENGTH_LONG).show();
        }
        return bos != null ? bos.toByteArray() : null;
    }
    private void reloadImages() {

        ArrayList<String> imagesByteArray = new ArrayList<>(DBHelper.getInstance(this).getAllImages());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setHasFixedSize(true);
        RVAdapter myAdapter = new RVAdapter(this,imagesByteArray);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
