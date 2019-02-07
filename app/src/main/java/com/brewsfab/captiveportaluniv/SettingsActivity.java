package com.brewsfab.captiveportaluniv;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.DialogPreference;
import android.util.Log;

import java.util.StringJoiner;


public class SettingsActivity extends AppCompatActivity implements EncryptedCredentialPrefDialogFragmentCompat.EncryptedCredentialListener, CryptoHelper.CredentialNeededListener {

    private KeyguardManager keyguardManager;
    private String username;
    private String password;
    private DialogPreference preference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        CryptoHelper.init(keyguardManager); //Init the crypto helper
        CryptoHelper.setmCredentialNeededListener(this);


        //Include de fragment
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }

    @Override
    public void onDialogPositiveClick(DialogPreference preference, String user, String pass) {
        Log.i("px", "user clik OK");
        //TODO try encrypting
        username = user;
        password = pass;
        this.preference = preference;
        Utils.Log("first attempt encrypting: " + encryptedCredentials(username, password));
    }

    @Override
    public void onDialogNegativeClick(DialogPreference preference) {
        Log.i("px", "user clik NON");

    }

    private String encryptedCredentials(String str1, String str2) {


        String concat = null;
        //Concatenate the credentials
        if (str2 != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                concat = new StringJoiner(":")
                        .add(str1)
                        .add(str2)
                        .toString();
            }
        } else {
            concat = str1;
        }

        Utils.Log("value of concat: " + concat);

        if (concat != null && !concat.isEmpty()) {
            //Start the encryption
            Utils.Log("inside check concat: " + concat);

            concat = CryptoHelper.encryptText(concat);
            if (concat != null) {
                if (concat.equals(CryptoHelper.USER_NOT_REGISTERED)) {

                    //TODO redo the encryption depending of the result of confirmcredentials
                    Utils.Log("not regggggg");
                } else if (concat.equals(CryptoHelper.ERROR_DEVICE_NOT_SECURE)) {
                    //TODO prompt the setting of device not secure
                    Utils.Log("device not secure");
                } else if (concat.equals(CryptoHelper.ERROR_CRYPTO)) {
                    //TODO manage the error crypto
                    Utils.Log("error crypto");
                } else {
                    //TODO manage the encrypted text
//                    setPreferenceText(concat);
                    if (preference != null) {
                        if (preference instanceof EncryptedCredentialPreference) {
                            EncryptedCredentialPreference encryptedCredentialPreference = ((EncryptedCredentialPreference) preference);
                            encryptedCredentialPreference.setText(concat);
                        }
                        Utils.Log("FINALLY ENCRYPTED");
                    }
                }

            }
        }

//        Utils.Log(concat);
        return concat;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Utils.Log("relaunched");

        if (requestCode == CryptoHelper.ENCRYPT_CREDENTIALS) {
            if (resultCode == RESULT_OK) {
                //TODO encrypt the text
                Utils.Log("Relaunched");
                encryptedCredentials(username, password);
            } else {
                //TODO dismiss the encryption process
                Utils.Log("Error with the code");
            }
        } else if (requestCode == CryptoHelper.DECRYPT_CREDENTIALS) {
            if (resultCode == RESULT_OK) {
                //TODO decrypt the text
            } else {
                //TODO dismiss the decryption process
            }
        }
    }

    @Override
    public void displayConfirmCredentials(int requestcode) {
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
        if (intent != null) {
            startActivityForResult(intent, requestcode);
        }
    }

    @Override
    public void successEncryption(String encryptedText) {
        Utils.Log("The ENCRYPTED: " + encryptedText);
    }

    @Override
    public void successDecryption(String decryptedText) {

    }

}
