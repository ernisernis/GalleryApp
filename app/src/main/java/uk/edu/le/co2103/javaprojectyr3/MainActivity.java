package uk.edu.le.co2103.javaprojectyr3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
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
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;


public class MainActivity extends AppCompatActivity {


    private static final String keyAlias = "key11";
    private static byte[] iv;
    Button clickMe, button2, lgn_btn;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            init();
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        clickMe = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        lgn_btn = findViewById(R.id.login_btn);

        clickMe.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this,SecondActivity.class);
//                intent.putExtra("Text", textHere);
            startActivity(intent);
        });

        button2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,ThirdActivity.class);
            startActivity(intent);
        });

        //Check if the user biometric authentication is available
//        BiometricManager biometricManager = new BiometricManager();
//        BiometricManager biometricManager = BiometricManager.from(this);
//        BiometricManager biometricManager = null;
//        switch (Objects.requireNonNull(biometricManager).canAuthenticate()) {
//            case BiometricManager.BIOMETRIC_SUCCESS:
//                Toast.makeText(MainActivity.this, "You can login. Success", Toast.LENGTH_SHORT).show();
//                break;
//
//            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                Toast.makeText(MainActivity.this, "The device does not have device print scanner", Toast.LENGTH_SHORT).show();
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                Toast.makeText(MainActivity.this, "The Scanner is not currently available", Toast.LENGTH_SHORT).show();
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                Toast.makeText(MainActivity.this, "The device currently does not have any fingerprint enrolled", Toast.LENGTH_SHORT).show();
//                break;
//        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,ThirdActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Use your fingerprint! to login!")
                .setNegativeButtonText("Cancel")
                .build();

        lgn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
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

    private void generateSecretKey() {

        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(keyAlias,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .build());
            keyGenerator.generateKey();
            System.out.println("SecretKey generated!");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
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

    private void init() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        SharedPreferences sharedPreferences = getSharedPreferences("encryptedInfo",MODE_PRIVATE);
        String checkEncArray = sharedPreferences.getString("encByteArray",null);
        if (checkEncArray == null) {
            Toast.makeText(MainActivity.this, "App is run for the first time!", Toast.LENGTH_LONG).show();
            // User launches the application for the first time. Create SecretKey, Create getPassword()
            // encrypt, save in SharedPreferences, <<<proceed with FingerPrint?>>>>

            // Generating a secretKey under certain alias, provided with global parameters
            generateSecretKey();

            // Generating a password and putting encrypted version of it into  byte[]
            String genPass = generatePassword();
            System.out.println(genPass);
            byte[] encryptedByteArray = encrypt(genPass);
//            byte[] encryptedByteArray = encrypt(generatePassword());

            // Save encrypted Byte Array to the Saved Preferences, with IV
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("encByteArray", Arrays.toString(encryptedByteArray));
            editor.putString("SharedIV", Arrays.toString(iv));
            editor.apply();
            System.out.println("Initialize Fingerprint and switch to ThirdActivity screen");
        } else {
            // The User launches the application NOT for the first time, proceed with fingerprint.
            Toast.makeText(MainActivity.this, "App is run NOT for the first time", Toast.LENGTH_LONG).show();
            System.out.println("Initialize Fingerprint and switch to ThirdActivity screen");
        }
    }

}