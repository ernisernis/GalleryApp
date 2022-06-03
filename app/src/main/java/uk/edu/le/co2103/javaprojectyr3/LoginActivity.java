package uk.edu.le.co2103.javaprojectyr3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.StrongBoxUnavailableException;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.Random;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class LoginActivity extends AppCompatActivity {


    private static final String keyAlias = "key11";
    private static byte[] iv;
    TextView txtView, appTitle, txtView2;
    LinearLayout lnrLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            init();
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        appTitle = findViewById(R.id.appTitle);
        txtView = findViewById(R.id.txtView);
        txtView2 = findViewById(R.id.txtView2);
        lnrLayout = findViewById(R.id.linearLayoutContainer);

        Typeface typeItalic = Typeface.createFromAsset(LoginActivity.this.getAssets(), "CabinItalic.ttf");
        txtView.setTextColor(Color.parseColor("#fcfdfb"));
        txtView.setTypeface(typeItalic);
        txtView2.setTextColor(Color.parseColor("#fcfdfb"));
        txtView2.setTypeface(typeItalic);

        Typeface type = Typeface.createFromAsset(LoginActivity.this.getAssets(), "CabinItalic.ttf");
        appTitle.setTextColor(Color.parseColor("#66a3ff"));
        appTitle.setTypeface(type);


        // Biometric authentication functions.
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                // Authentication error
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(LoginActivity.this, "Error code and Error String respectively: " + errorCode + " " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                // Authentication succeeded
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(LoginActivity.this,FolderActivity.class);
                startActivity(intent);
            }

            // Authentication failed
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Use your fingerprint")
                .setNegativeButtonText("Cancel")
                .build();


        lnrLayout.setOnClickListener(view -> {
            // Invoking biometric prompt by clicking on the fingerprint icon
            biometricPrompt.authenticate(promptInfo);
        });
    }

    private static String generatePassword(){
        String letters = "ABCDEFGHIJKLMNOPRQSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChar = "!@#$%^&*";

        String pwa = letters+numbers+specialChar;
        Random r = new Random();
        // Generating random 32 letter long password.
        char[] newPass = new char[32];
        for (int i = 0; i<32;i++) {
            newPass[i] = pwa.charAt(r.nextInt(pwa.length()));
        }
        return new String(newPass);
    }

    private void generateSecretKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

        // Using KeyGenerator library to create the key
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(keyAlias,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setIsStrongBoxBacked(true)
                            .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (StrongBoxUnavailableException e) {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(keyAlias,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .build());
            keyGenerator.generateKey();
        }
    }

    private static SecretKey getSecretKey() {
        // Function to receive secret key, used by encrypt function
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

    private static byte[] encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // encrypting the data with previously generated key
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        iv = cipher.getIV();
        return cipher.doFinal(data.getBytes());
    }

    private void init() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {

        // Do nothing here if the user has NOT launched the app for the first time

        SharedPreferences sharedPreferences = getSharedPreferences("encryptedInfo",MODE_PRIVATE);
        String checkEncArray = sharedPreferences.getString("encByteArray",null);
        // Check whether the user has launched the app for the first time by trying to get sharedPreference values
        if (checkEncArray == null) {
            // User launches the app for the first time

            // Generating the key
            generateSecretKey();

            // Encrypting randomly generated password
            byte[] encryptedByteArray = encrypt(generatePassword());

            // Save encrypted Byte Array, IV to the Saved Preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("encByteArray", Arrays.toString(encryptedByteArray));
            editor.putString("SharedIV", Arrays.toString(iv));
            editor.apply();
        }

    }

}
