package com.brewsfab.captiveportaluniv;

import android.app.KeyguardManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoHelper {

//    private Context context;

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String CAPTIVE_WIFI_KEY = "CaptiveWifiKey";
    public static final String USER_NOT_REGISTERED = "user_not_registered";
    public static final int ENCRYPT_CREDENTIALS = 10;
    public static final int DECRYPT_CREDENTIALS = 11;
    public static final String ERROR_DEVICE_NOT_SECURE = "device_not_secure";
    public static final String ERROR_CRYPTO = "error_crypto";

    private static CredentialNeededListener mCredentialNeededListener;
    private static KeyguardManager keyguardManager;


    public static void init(KeyguardManager kgm) {
        keyguardManager = kgm;
    }


    public static void setmCredentialNeededListener(CredentialNeededListener credentialNeededListener) {
        mCredentialNeededListener = credentialNeededListener;
    }

    public static void unsetmCredentialNeededListener(CredentialNeededListener credentialNeededListener) {
        if (credentialNeededListener != null)
            mCredentialNeededListener = null;
    }


    public static String encryptText(String clearText) {

        if (!keyguardManager.isDeviceSecure()) {
            Utils.Log("the device is not secured");
            return ERROR_DEVICE_NOT_SECURE;
        }

        try {
            SecretKey secretKey = createKey();
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptionIV = cipher.getIV();
            byte[] textToBytes = clearText.getBytes("UTF-8");

            byte[] encryptedTextToBytes = cipher.doFinal(textToBytes);

            String encryptedCred = Base64.encodeToString(encryptedTextToBytes, Base64.DEFAULT);
            String encrypteddIv = Base64.encodeToString(encryptionIV, Base64.DEFAULT);

            String encryptedWhole = encryptedCred + "]" +
                    encrypteddIv;
            Utils.Log(encryptedWhole);
            mCredentialNeededListener.successEncryption(encryptedWhole);
            return encryptedWhole;
        } catch (UserNotAuthenticatedException e) {
            Utils.Log("when user not authenticated, text is: " + clearText);
            mCredentialNeededListener.displayConfirmCredentials(ENCRYPT_CREDENTIALS);
            return USER_NOT_REGISTERED;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return ERROR_CRYPTO;
        }

    }


    public static String decryptText(String encryptedText) {

        if (!keyguardManager.isDeviceSecure()) {
            return ERROR_DEVICE_NOT_SECURE;
        }

        String[] credentials = encryptedText.split("]", 2);
        byte[] encryptedIVBytes = Base64.decode(credentials[1], Base64.DEFAULT);
        byte[] encryptedCreds = Base64.decode(credentials[0], Base64.DEFAULT);

        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(CAPTIVE_WIFI_KEY, null);

            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(encryptedIVBytes));
            byte[] credBytes = cipher.doFinal(encryptedCreds);
            String decryptedText = new String(credBytes, "UTF-8");

            mCredentialNeededListener.successDecryption(new String(credBytes, "UTF-8"));
            return decryptedText;

        } catch (UserNotAuthenticatedException e) {
            mCredentialNeededListener.displayConfirmCredentials(DECRYPT_CREDENTIALS);
            return USER_NOT_REGISTERED;
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchPaddingException | UnrecoverableKeyException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return ERROR_CRYPTO;
        }

    }


    private static SecretKey createKey() {

        if (!keyguardManager.isDeviceSecure()) {
            return null;
        }

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(new KeyGenParameterSpec.Builder(CAPTIVE_WIFI_KEY, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(5) //set to -1 to authenticate everytime
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to create a symmetric key");
        }
    }


    public interface CredentialNeededListener {
        void displayConfirmCredentials(int requestCode);

        void successEncryption(String encryptedText);

        void successDecryption(String decryptedText);
    }
}


