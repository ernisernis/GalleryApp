package uk.edu.le.co2103.javaprojectyr3;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
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

    // Fabs functionalities
    FloatingActionButton fabBtn_Main, fabBtn_Add, fabBtn_Gallery;
    TextView addPhotoText, takePhotoText;
    Boolean isAllFabsVisible;

    String currentPhotoPath;
    Button goBack;
    private RecyclerView recyclerView;
    ProgressDialog p;

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

//        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
//        recyclerView.setLayoutManager(layoutManager);

        goBack = findViewById(R.id.button);

        fabBtn_Main.setOnClickListener(view -> mainFabButton());

        fabBtn_Add.setOnClickListener(view -> {
            askCameraPermissions();
            hideFabsAndText();
        });

        fabBtn_Gallery.setOnClickListener(view -> {
            Toast.makeText(ThirdActivity.this, "Initialize gallery add procedure", Toast.LENGTH_SHORT).show();
            galleryImage();
            hideFabsAndText();
        });

//        try {
//            reloadImages();
//        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
//            e.printStackTrace();
//        }

        goBack.setOnClickListener(view -> {
            Intent intent = new Intent(ThirdActivity.this,MainActivity.class);
            startActivity(intent);
        });

        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
        myAsyncTasks.execute();

//        try {
//            ArrayList<String> imagesByteArray = new ArrayList<>(DBHelper.getInstance(ThirdActivity.this).getAllImages(passwordToDb()));
//            if (imagesByteArray.size() != 0) {
//                System.out.println(imagesByteArray.size());
//            }
//        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }

    }

    public class MyAsyncTasks extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(ThirdActivity.this);
            p.setMessage("Please wait. Decrypting...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//                reloadImages();
                ArrayList<String> imagesByteArray = new ArrayList<>(DBHelper.getInstance(ThirdActivity.this).getAllImages(passwordToDb()));
                if (imagesByteArray.size() == 0) {
                    return "empty";
                }
                ArrayList<Bitmap> bitmapArray = new ArrayList<>();
                for(int i = 0; i < imagesByteArray.size(); i++) {
                    // Single image byte string
                    String sIBS = imagesByteArray.get(i);
                    sIBS = sIBS.substring(0, sIBS.length() -1);
                    sIBS = sIBS.substring(1);
                    String [] bytesString = sIBS.split(", ");
                    byte [] bytes = new byte[bytesString.length];
                    for(int j = 0 ; j < bytes.length ; ++j) {
                        bytes[j] = Byte.parseByte(bytesString[j]);
                    }
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bitmapArray.add(bmp);
                }

                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                recyclerView.setHasFixedSize(true);
                RVAdapter myAdapter = new RVAdapter(ThirdActivity.this,bitmapArray);
                GridLayoutManager layoutManager = new GridLayoutManager(ThirdActivity.this,2);
//        RVAdapter myAdapter = new RVAdapter(this,imagesByteArray);
                runOnUiThread(() -> {

                    // Stuff that updates the UI
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setLayoutManager(layoutManager);
                });
//                RVAdapter myAdapter = new RVAdapter(ThirdActivity.this,bitmapArray);
//                recyclerView.setAdapter(myAdapter);
//                GridLayoutManager layoutManager = new GridLayoutManager(ThirdActivity.this,2);
//                recyclerView.setLayoutManager(layoutManager);
                return "accepted";
            } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
            return "notAccepted";
        }

        @Override
        protected void onPostExecute(String string) {
//            super.onPostExecute("accepted");
            System.out.println(string);
            if (string.equals("accepted") || string.equals("empty")) {
                p.hide();
            }
//            p.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if(requestCode == CAMERA_REQUEST_CODE) {
                try {
                    DBHelper.getInstance(ThirdActivity.this).insertNewImage(Arrays.toString(readFile(currentPhotoPath)), passwordToDb());
                    reloadImages();
                } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == GALLERY_SELECT_PICTURE) {
                System.out.println(data.getData());
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    selectedImage.recycle();
                    DBHelper.getInstance(ThirdActivity.this).insertNewImage(Arrays.toString(byteArray),passwordToDb());
                    reloadImages();
                } catch (FileNotFoundException | NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                    Toast.makeText(ThirdActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }


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
        } catch (IOException e) {
            Toast.makeText(ThirdActivity.this, "Error reading a photo! : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return bos != null ? bos.toByteArray() : null;
    }

    private void reloadImages() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        ArrayList<String> imagesByteArray = new ArrayList<>(DBHelper.getInstance(this).getAllImages(passwordToDb()));
        ArrayList<Bitmap> bitmapArray = new ArrayList<>();
        for(int i = 0; i < imagesByteArray.size(); i++) {
            // Single image byte string
            String sIBS = imagesByteArray.get(i);
            sIBS = sIBS.substring(0, sIBS.length() -1);
            sIBS = sIBS.substring(1);
            String [] bytesString = sIBS.split(", ");
            byte [] bytes = new byte[bytesString.length];
            for(int j = 0 ; j < bytes.length ; ++j) {
                bytes[j] = Byte.parseByte(bytesString[j]);
            }
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            bitmapArray.add(bmp);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setHasFixedSize(true);
//        RVAdapter myAdapter = new RVAdapter(this,imagesByteArray);
        RVAdapter myAdapter = new RVAdapter(this,bitmapArray);
        recyclerView.setAdapter(myAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
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

}
