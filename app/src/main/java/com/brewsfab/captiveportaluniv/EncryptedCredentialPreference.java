package com.brewsfab.captiveportaluniv;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

public class EncryptedCredentialPreference extends DialogPreference {

    private int mEncryptedCredentialPreferenceId = R.layout.encrypted_credential_pref;

    public EncryptedCredentialPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public EncryptedCredentialPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,defStyleAttr);
    }

    public EncryptedCredentialPreference(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EncryptedCredentialPreference(Context context) {
        this(context,null);
    }


    @Override
    public int getDialogLayoutResource() {
        return mEncryptedCredentialPreferenceId;
    }


    public void setText(String text) {
        persistString(text);
    }


    //Todo add the setText method for the username and password

}
