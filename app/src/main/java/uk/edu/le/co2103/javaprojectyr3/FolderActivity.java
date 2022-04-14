package uk.edu.le.co2103.javaprojectyr3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;

public class FolderActivity extends AppCompatActivity {

    FloatingActionButton addFolder;
//    Button glowButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    String dbPass;
    // For recyclerView
    private ArrayList<Folder> folders = new ArrayList<>();
    // For retrieving from DB
    private static final String keyAlias = "key11";
    ProgressDialog p;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);


        recyclerView = findViewById(R.id.folderRecyclerView);
        ArrayList<Folder> emptyFolder = new ArrayList<>();
        adapter = new FolderAdapter(FolderActivity.this,emptyFolder,dbPass);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // For DB
        SQLiteDatabase.loadLibs(this);



        addFolder = findViewById(R.id.addFolderButton);
        addFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FolderActivity.this,AddFolderActivity.class);
                startActivity(intent);
            }
        });

        MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
        myAsyncTasks.execute();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                System.out.println("Has back pressed!!!");
                FolderAdapter.getInstance(FolderActivity.this,folders,dbPass).clear();
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                System.out.println("Result cancelled!!!");
            }
        }
    }

    public class MyAsyncTasks extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(FolderActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            // Getting the folder names, first image, count from the DB
            try {
                dbPass = passwordToDb();
                ArrayList<String> folderNamesDB = new ArrayList<>(DBHelper.getInstance(FolderActivity.this).getAllFoldersStringArray(dbPass));
                // Putting the folder names to the Object (folder) list
                for (int i = 0; i < folderNamesDB.size(); i++) {
                    int count = DBHelper.getInstance(FolderActivity.this).getFolderImageCount(dbPass, folderNamesDB.get(i));
                    byte[] firstImage = DBHelper.getInstance(FolderActivity.this).getFirstFolderImage(dbPass, folderNamesDB.get(i));
                    Folder folder = new Folder(folderNamesDB.get(i), count, firstImage);
                    folders.add(folder);
                }
            } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {

                adapter = new FolderAdapter(FolderActivity.this, folders,dbPass);
                recyclerView.setAdapter(adapter);

                Typeface type = Typeface.createFromAsset(getAssets(), "Cabin.ttf");
                TextView albumText = findViewById(R.id.albumText);
                albumText.setTypeface(type);
                albumText.setTextColor(Color.parseColor("#fcfdfb"));

            });
            // Adapter
            return "accepted";
        }

        @Override
        protected void onPostExecute(String string) {
            if (string.equals("accepted")) {
                p.dismiss();
            }
        }

    }
}
