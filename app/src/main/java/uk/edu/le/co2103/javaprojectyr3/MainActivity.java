package uk.edu.le.co2103.javaprojectyr3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;


public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;

    private static final String keyAlias = "key49";
    private static byte[] iv;
    private static byte[] encryptedByteArray;
    Button clickMe, button2, button3,button4, button5, button6,button7, button8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickMe = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);

        clickMe.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this,SecondActivity.class);
//                intent.putExtra("Text", textHere);
            startActivity(intent);
        });

        button2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,ThirdActivity.class);
            startActivity(intent);
        });

        button3.setOnClickListener(view -> System.out.println(generateSecretKey()));

        button4.setOnClickListener(view -> System.out.println(getSecretKey()));

        button5.setOnClickListener(v -> {

            SharedPreferences sharedPreferences = getSharedPreferences("encryptedInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("encByteArray", Arrays.toString(encryptedByteArray));
            editor.putString("SharedIV", Arrays.toString(iv));
            editor.commit();
//            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedpreferences.edit();
//            editor.putString("nameKey", "Hello");
//            editor.commit();
            Toast.makeText(MainActivity.this, "Button5 Pressed!", Toast.LENGTH_SHORT).show();
        });

        button6.setOnClickListener(v -> {
            System.out.println(generatePassword());
            Toast.makeText(MainActivity.this, "Button6 clicked!", Toast.LENGTH_SHORT).show();
        });

        button7.setOnClickListener(view -> {
            try {
                String password = generatePassword();
                System.out.println(password + " <- generated password");
                encryptedByteArray = encrypt(password);
                System.out.println(Arrays.toString(encryptedByteArray));
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this, "Button7 or Encryption pressed!", Toast.LENGTH_SHORT).show();
        });

        button8.setOnClickListener(view -> {
            try {
                System.out.println(decrypt(encryptedByteArray));
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this, "Button 8 or Decryption Pressed!", Toast.LENGTH_SHORT).show();
        });
    }

    private static String generatePassword(){
        String letters = "ABCDEFGHIJKLMNOPRQSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChar = "!@#$%^&*";

        String pwa = letters+numbers+specialChar;
        Random r = new Random();
        char[] newPass = new char[32];
        for (int i = 0; i<32;i++) {
            newPass[i] = pwa.charAt(r.nextInt(pwa.length()));
        }
        return new String(newPass);
    }

    private static SecretKey generateSecretKey() {

        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(keyAlias,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .build());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    return keyGenerator.generateKey();
    }

    private static SecretKey getSecretKey() {

        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return (SecretKey) keyStore.getKey(keyAlias,null);

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        iv = cipher.getIV();
        return cipher.doFinal(data.getBytes());
    }

    private static String decrypt (byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE,getSecretKey(),spec);
        byte[] decoded = cipher.doFinal(encrypted);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}