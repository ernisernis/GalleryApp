package uk.edu.le.co2103.javaprojectyr3;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;

public class ThirdActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 121; //These numbers does not really matter, as long as they are different it's good.
    public static final int CAMERA_REQUEST_CODE = 131; //These numbers does not really matter, as long as they are different it's good.
    public static final int GALLERY_SELECT_PICTURE = 141;
    private static final String keyAlias = "key11";
    private String finalPw;

    RecyclerTouchListener recyclerview;
    ImageView imageView;
    RVAdapter myAdapter;


    // Fabs functionalities
    FloatingActionButton fabBtn_Main, fabBtn_Add, fabBtn_Gallery;
    TextView addPhotoText, takePhotoText;
    Boolean isAllFabsVisible;

    String currentPhotoPath;
    //    Button goBack;
    private RecyclerView recyclerView;
    ProgressDialog p;

    ArrayList<byte[]> imagesBytesDB;
    ArrayList<Bitmap> bitmapArrayDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // For DB
        SQLiteDatabase.loadLibs(this);


        // For all the FAB'S
        addPhotoText = findViewById(R.id.addPhotoText);
        takePhotoText = findViewById(R.id.takePhotoText);
        fabBtn_Main = findViewById(R.id.fab_main);
        fabBtn_Add = findViewById(R.id.fab_takePhoto);
        fabBtn_Gallery = findViewById(R.id.fab_addPhoto);
        fabBtn_Add.setVisibility(View.GONE);
        fabBtn_Gallery.setVisibility(View.GONE);
        addPhotoText.setVisibility(View.GONE);
        takePhotoText.setVisibility(View.GONE);
        isAllFabsVisible = false;

        fabBtn_Main.setOnClickListener(view -> mainFabButton());

        fabBtn_Add.setOnClickListener(view -> {
            askCameraPermissions();
            hideFabsAndText();
        });

        fabBtn_Gallery.setOnClickListener(view -> {
            galleryImage();
            hideFabsAndText();
        });


        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
        myAsyncTasks.execute();

    }

    public class MyAsyncTasks extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(ThirdActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                finalPw = passwordToDb();
                System.out.println(finalPw);
                imagesBytesDB = new ArrayList<>(DBHelper.getInstance(ThirdActivity.this).getAllImagesByteArray(finalPw));
                bitmapArrayDB = new ArrayList<>();
                for (int i = 0; i < imagesBytesDB.size(); i++) {
                    byte[] placeHolder = imagesBytesDB.get(i);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(placeHolder,0,placeHolder.length,options);
                    bitmapArrayDB.add(bitmap);
                }

                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                recyclerView.setHasFixedSize(true);
                myAdapter = new RVAdapter(ThirdActivity.this,bitmapArrayDB);
                GridLayoutManager layoutManager = new GridLayoutManager(ThirdActivity.this,4);
                runOnUiThread(() -> {

                    // Stuff that updates the UI
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addOnItemTouchListener(new RecyclerTouchListener(ThirdActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
                        // Invokes onClick when a single photo gets clicked.
                        @Override
                        public void onClick(View view, int position) {
                            singleImageClick(view,position);
                        }

                        @Override
                        public void onLongClick(View view, int position) {
                            singleImageLongClick(view,position);
                        }
                    }));
                });



                return "accepted";
            } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
            return "notAccepted";
        }

        @Override
        protected void onPostExecute(String string) {
            System.out.println(string);
            if (string.equals("accepted") || string.equals("empty")) {
                p.hide();
            }
        }
    }


    public class MyAsyncTaskReload extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(ThirdActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                int position = Integer.parseInt(strings[0]);
                finalPw = passwordToDb();
                DBHelper.getInstance(ThirdActivity.this).deleteImage(finalPw, imagesBytesDB.get(position));
                runOnUiThread(() -> {
                    bitmapArrayDB.remove(position);
                    myAdapter.notifyItemRemoved(position);
                    myAdapter.notifyItemRangeChanged(position,bitmapArrayDB.size());
                });
                return "accepted";
            } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
            return "notAccepted";
        }

        @Override
        protected void onPostExecute(String string) {
            System.out.println(string);
            if (string.equals("accepted") || string.equals("empty")) {
                p.hide();
            }
        }
    }


    public class MyAsyncTasksGallery extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(ThirdActivity.this);
            p.setMessage("Please wait...!!!!!");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String stringUri = strings[0];
                Uri imageUri = Uri.parse(stringUri);
                InputStream iStream = getContentResolver().openInputStream(imageUri);
                byte[] inputData = getBytes(iStream);
                DBHelper.getInstance(ThirdActivity.this).insertNewImageBlob(inputData,finalPw);
                return "proceed";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "failed";
        }

        @Override
        protected void onPostExecute(String string) {
            if (string.equals("proceed"))  {
                Toast.makeText(ThirdActivity.this, "Uploading image suceeded!!!", Toast.LENGTH_LONG).show();
                p.hide();
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
            } else {
                p.hide();
                Toast.makeText(ThirdActivity.this, "Uploading image failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if(requestCode == CAMERA_REQUEST_CODE) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();
                bitmap.recycle();
                DBHelper.getInstance(ThirdActivity.this).insertNewImageBlob(byteArray,finalPw);
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
            }

            if (requestCode == GALLERY_SELECT_PICTURE) {

                final Uri imageUri = data.getData();
                    String imageStrUri = imageUri.toString();
                    MyAsyncTasksGallery myAsyncTasksGallery = new MyAsyncTasksGallery();
                    myAsyncTasksGallery.execute(imageStrUri);
            }


        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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

    private static SecretKey getSecretKey() {

        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return (SecretKey) keyStore.getKey(keyAlias,null);

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String decrypt (byte[] encrypted, byte[] pwIv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, pwIv);
        cipher.init(Cipher.DECRYPT_MODE,getSecretKey(),spec);
        byte[] decoded = cipher.doFinal(encrypted);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private String passwordToDb() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        SharedPreferences settings = getSharedPreferences("encryptedInfo",MODE_PRIVATE);
        String byteArray = settings.getString("encByteArray",null);
        String byteIv = settings.getString("SharedIV",null);
        return decrypt(strToByteArrConverter(byteArray),strToByteArrConverter(byteIv));
    }

    private byte[] strToByteArrConverter(String data) {
        String[] split = data.substring(1, data.length()-1).split(", ");
        byte[] array = new byte[split.length];
        for (int i = 0; i < split.length; i++) {
            array[i] = Byte.parseByte(split[i]);
        }
        return array;
    }

    private void mainFabButton() {
        if (!isAllFabsVisible) {
            addPhotoText.setVisibility(View.VISIBLE);
            takePhotoText.setVisibility(View.VISIBLE);
            fabBtn_Gallery.show();
            fabBtn_Add.show();
            isAllFabsVisible = true;
        } else {
            hideFabsAndText();
        }
    }

    private void hideFabsAndText() {
        fabBtn_Add.hide();
        fabBtn_Gallery.hide();
        addPhotoText.setVisibility(View.GONE);
        takePhotoText.setVisibility(View.GONE);
        isAllFabsVisible = false;
    }

    private void galleryImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), GALLERY_SELECT_PICTURE);
    }

    private void singleImageClick(View view, int position) {
        imageView = findViewById(R.id.singleImage);
        imageView.setImageBitmap(bitmapArrayDB.get(position));

        imageView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        fabBtn_Main.setVisibility(View.GONE);

        imageView.setOnClickListener(view1 -> {
            imageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            fabBtn_Main.setVisibility(View.VISIBLE);
        });
    }

    private void singleImageLongClick(View view, int position) {

        Toast.makeText(ThirdActivity.this, "Long message clicked", Toast.LENGTH_SHORT).show();
//        imagesBytesDB = new ArrayList<>(DBHelper.getInstance(ThirdActivity.this).getAllImagesByteArray(finalPw));
//        DBHelper.getInstance(ThirdActivity.this).deleteImage(finalPw, imagesBytesDB.get(position));
//        recyclerView.removeItemDecorationAt(position);
//        bitmapArrayDB.remove(position);
//        notifyItemRemoved(position);
//        DBHelper.getInstance(ThirdActivity.this).deleteImage(finalPw, imagesBytesDB.get(position));
//        bitmapArrayDB.remove(position);
//        myAdapter.notifyItemRemoved(position);
//        myAdapter.notifyItemRangeChanged(position,bitmapArrayDB.size());
        MyAsyncTaskReload myAsyncTaskReload = new MyAsyncTaskReload();
        myAsyncTaskReload.execute(Integer.toString(position));
    }
}
