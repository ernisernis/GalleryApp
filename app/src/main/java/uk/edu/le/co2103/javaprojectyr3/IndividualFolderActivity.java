package uk.edu.le.co2103.javaprojectyr3;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;

public class IndividualFolderActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 121; //These numbers does not really matter, as long as they are different it's good.
    public static final int CAMERA_REQUEST_CODE = 131; //These numbers does not really matter, as long as they are different it's good.
    public static final int GALLERY_SELECT_PICTURE = 141;
    private static final String keyAlias = "key11";
    private String finalPw;


    ImageView imageView, backButton;
    RVAdapter myAdapter;


    int changedActivityStatus = 0;


    // Fabs functionalities
    FloatingActionButton fabBtn_Main, fabBtn_Add, fabBtn_Gallery;
    TextView addPhotoText, takePhotoText;
    Boolean isAllFabsVisible;

    CardView imageCardView;
    TextView folderTitle;
    TextView folderCountNumber;
    ImageView folderImage;
    int folderCount;
    String currentPhotoPath;
    String folderName;
    String folderCountString;
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

        // For RecyclerView. Initializing empty recyclerView and adapter to avoid the error.
        recyclerView = findViewById(R.id.recyclerView);
        ArrayList<Bitmap> emptyBitmap = new ArrayList<>();
        ArrayList<byte[]> emptyByteArr = new ArrayList<>();
        folderName = getIntent().getExtras().getString("FOLDERS_NAME","defaultKey");
        folderCount = getIntent().getExtras().getInt("PHOTO_COUNT", 0);
        try {
            finalPw = passwordToDb();
        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        myAdapter = new RVAdapter(IndividualFolderActivity.this,emptyBitmap,finalPw, folderName, emptyByteArr, folderCount);
        recyclerView.setAdapter(myAdapter);
        GridLayoutManager layoutManager2 = new GridLayoutManager(IndividualFolderActivity.this,1);
        recyclerView.setLayoutManager(layoutManager2);
        backButton = findViewById(R.id.backButton);


        // For all the FAB'S
        imageCardView = findViewById(R.id.imageCardView);
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
        Typeface type = Typeface.createFromAsset(IndividualFolderActivity.this.getAssets(), "Cabin.ttf");
        addPhotoText.setTextColor(Color.parseColor("#fcfdfb"));
        takePhotoText.setTextColor(Color.parseColor("#fcfdfb"));
        addPhotoText.setTypeface(type);
        takePhotoText.setTypeface(type);

        fabBtn_Main.setOnClickListener(view -> mainFabButton());


        fabBtn_Add.setOnClickListener(view -> {
            askCameraPermissions();
            hideFabsAndText();
        });

        fabBtn_Gallery.setOnClickListener(view -> {
            galleryImage();
            hideFabsAndText();
        });

        backButton.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            if (changedActivityStatus == 1) {
                setResult(Activity.RESULT_OK,returnIntent);
            } else {
                setResult(Activity.RESULT_CANCELED,returnIntent);
            }
            finish();
        });


        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
        myAsyncTasks.execute();

    }

    public class MyAsyncTasks extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(IndividualFolderActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... strings) {
//                finalPw = passwordToDb();
                imagesBytesDB = new ArrayList<>(DBHelper.getInstance(IndividualFolderActivity.this).getAllImagesByteArray(finalPw,folderName));
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
                myAdapter = new RVAdapter(IndividualFolderActivity.this,bitmapArrayDB,finalPw, folderName, imagesBytesDB, folderCount);
                GridLayoutManager layoutManager = new GridLayoutManager(IndividualFolderActivity.this,4);
                runOnUiThread(() -> {

                    folderImage = findViewById(R.id.folderImage);
                    folderTitle = findViewById(R.id.folderTitle);
                    folderCountNumber = findViewById(R.id.folderCountNumber);
                    if (bitmapArrayDB.size()!=0) {
                        folderImage.setImageBitmap(Bitmap.createScaledBitmap(bitmapArrayDB.get(0),80,80,false));
                    }
                    folderTitle.setText(folderName);
                    Typeface type = Typeface.createFromAsset(IndividualFolderActivity.this.getAssets(), "Cabin.ttf");
                    Typeface typeItalic = Typeface.createFromAsset(IndividualFolderActivity.this.getAssets(), "CabinItalic.ttf");
                    folderTitle.setTextColor(Color.parseColor("#fcfdfb"));
                    folderTitle.setTypeface(type);
                    folderCountNumber.setTextColor(Color.parseColor("#a6a6a6"));
                    folderCountNumber.setTypeface(typeItalic);
                    folderCountString = "Photo count: " + folderCount;
                    folderCountNumber.setText(folderCountString);
                    // Functions that updates the UI
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addOnItemTouchListener(new RecyclerTouchListener(IndividualFolderActivity.this, recyclerView, IndividualFolderActivity.this::singleImageClick));
                });

                return "accepted";
        }

        @Override
        protected void onPostExecute(String string) {
            if (string.equals("accepted") || string.equals("empty")) {
                p.dismiss();
            }
        }
    }


    public class MyAsyncTasksGallery extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(IndividualFolderActivity.this);
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
                DBHelper.getInstance(IndividualFolderActivity.this).insertNewImageBlob(inputData,finalPw, folderName);
                return "proceed";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "failed";
        }

        @Override
        protected void onPostExecute(String string) {
            if (string.equals("proceed"))  {
                p.dismiss();
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
            } else {
                p.dismiss();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if(requestCode == CAMERA_REQUEST_CODE) {
                folderCount++;
                changedActivityStatus = 1;
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();
                bitmap.recycle();
                DBHelper.getInstance(IndividualFolderActivity.this).insertNewImageBlob(byteArray,finalPw, folderName);
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
            }

            if (requestCode == GALLERY_SELECT_PICTURE) {
                folderCount++;
                changedActivityStatus = 1;
                assert data != null;
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
                Toast.makeText(IndividualFolderActivity.this, "Error processing image file! : " + ex, Toast.LENGTH_LONG).show();
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

    // Hide the UI whenever the user clicks on the individual photo, and unhide when he clicks on it again.
    private void singleImageClick(View view, int position) {
        imageView = findViewById(R.id.singleImage);
        imageView.setImageBitmap(bitmapArrayDB.get(position));

        imageView.setVisibility(View.VISIBLE);
        imageCardView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        fabBtn_Main.setVisibility(View.GONE);

        imageView.setOnClickListener(view1 -> {
            imageView.setVisibility(View.GONE);
            imageCardView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            fabBtn_Main.setVisibility(View.VISIBLE);
        });
    }

    // Check if an activity needs to be updated (if user performed adding photos/deletion of photos
    @Override
    public void onBackPressed() {
        if (changedActivityStatus == 1) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        } else {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED,returnIntent);
            finish();
        }

    }

    // This method is called by RVAdapter to update the Photo count after the image gets deleted
    public void putCount(int count) {
        String folderCountNr = "Photo count: " + count;
        folderCount = count;
        folderCountNumber.setText(folderCountNr);
    }

    // This method is called by RVAdapter to change the activity status after deleting a single image
    public void changeActivityStatus() {
        changedActivityStatus = 1;
    }

    // This method gets called by RVAdapter to change the album icons after the first photo of the album gets deleted
    public void changeFolderImageBubble() {
        if (bitmapArrayDB.size() >= 2) {
            folderImage.setImageBitmap(Bitmap.createScaledBitmap(bitmapArrayDB.get(1),80,80,false));
        } else {
            folderImage.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
        }
    }

}
