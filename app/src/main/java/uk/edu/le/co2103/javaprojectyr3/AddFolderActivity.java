package uk.edu.le.co2103.javaprojectyr3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class AddFolderActivity extends AppCompatActivity {

    TextView cancelText, albumText, createText, albumTextView;
    EditText inputFolderName;
    String dbPassword;
    private static final String keyAlias = "key11";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfolder);

        // For DB
        SQLiteDatabase.loadLibs(this);

        // Password for DB
        try {
            dbPassword = passwordToDb();
        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        inputFolderName = findViewById(R.id.inputName);
        cancelText = findViewById(R.id.cancelText);
        albumText = findViewById(R.id.albumText);
        createText = findViewById(R.id.createText);
        albumTextView = findViewById(R.id.albumTextView);

        Typeface type = Typeface.createFromAsset(AddFolderActivity.this.getAssets(), "Cabin.ttf");
        cancelText.setTextColor(Color.parseColor("#fcfdfb"));
        albumText.setTextColor(Color.parseColor("#fcfdfb"));
        createText.setTextColor(Color.parseColor("#fcfdfb"));
        albumTextView.setTextColor(Color.parseColor("#fcfdfb"));
        inputFolderName.setTextColor(Color.parseColor("#fcfdfb"));
        cancelText.setTypeface(type);
        albumText.setTypeface(type);
        createText.setTypeface(type);
        albumTextView.setTypeface(type);
        inputFolderName.setTypeface(type);

        createText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get name of the folder and create DB table with PK as a folder name. Create 3 columns.
//                imagesBytesDB = new ArrayList<>(DBHelper.getInstance(ThirdActivity.this).getAllImagesByteArray(finalPw));
                if (!inputFolderName.getText().toString().equals("")) {
                    Toast.makeText(AddFolderActivity.this, inputFolderName.getText().toString(), Toast.LENGTH_SHORT).show();
                    DBHelper.getInstance(AddFolderActivity.this).createFolder(dbPassword, inputFolderName.getText().toString());
                    Intent intent = new Intent(AddFolderActivity.this,FolderActivity.class);
                    startActivity(intent);
                }
            }
        });

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        createButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//
//        testFolders.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ArrayList<String> folderString = new ArrayList<>(DBHelper.getInstance(AddFolderActivity.this).getAllFoldersStringArray(dbPassword));
//                for (int i = 0; i < folderString.size(); i++) {
//                    System.out.println(folderString.get(i));
//                }
//            }
//        });


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


}
