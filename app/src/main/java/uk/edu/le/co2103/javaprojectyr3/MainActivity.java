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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class MainActivity extends AppCompatActivity {

    private static final String keyAlias = "key49";
    Button clickMe, button2, button3,button4, button5, button6;


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

        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
//                intent.putExtra("Text", textHere);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ThirdActivity.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(generateSecretKey());
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyStore keyStore = null;
                try {
                    keyStore = KeyStore.getInstance("AndroidKeyStore");
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }
                try {
                    keyStore.load(null);
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                    KeyStore keyStoreA = KeyStore.getInstance("AndroidKeyStore");
                    keyStoreA.load(null);
                    System.out.println("-------------");
                    System.out.println(keyStoreA.containsAlias("key6"));
                    System.out.println("-------------");
                    SecretKey keyA = (SecretKey) keyStoreA.getKey("key6",null);
                    System.out.println(keyA);

                    System.out.println("0000000000000000000000");

                    KeyStore keyStoreB = KeyStore.getInstance("AndroidKeyStore");
                    keyStoreB.load(null);
                    System.out.println("-------------");
                    System.out.println(keyStoreB.containsAlias("key6"));
                    System.out.println("-------------");
//                    System.out.println(keyStoreB.getKey("key5",null));
                    SecretKey keyB = (SecretKey) keyStoreB.getKey("key6",null);
                    System.out.println(keyB);
                    ////////////
//                    SecretKey key = (SecretKey) keyStore.getKey("key2", null);
//                    Toast.makeText(MainActivity.this, (CharSequence) key, Toast.LENGTH_SHORT).show();
//                    System.out.println(key);
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("nameKey", "Hello");
                editor.commit();
                Toast.makeText(MainActivity.this, "Button5 Pressed!", Toast.LENGTH_SHORT).show();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char[] abc = generatePassword();
//                System.out.println(Arrays.toString(abc));
                String stringPW = new String(abc);
                System.out.println(stringPW);
                Toast.makeText(MainActivity.this, "Button6 clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private static char[] generatePassword(){
        String letters = "ABCDEFGHIJKLMNOPRQSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChar = "!@#$%^&*";

        String pwa = letters+numbers+specialChar;
        Random r = new Random();
        char[] newPass = new char[32];
        for (int i = 0; i<32;i++) {
            newPass[i] = pwa.charAt(r.nextInt(pwa.length()));
        }
        return newPass;
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
//                                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .build());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    return keyGenerator.generateKey();
    }
}