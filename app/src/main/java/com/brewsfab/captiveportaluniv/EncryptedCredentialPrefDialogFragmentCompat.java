package com.brewsfab.captiveportaluniv;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.EditText;

public class EncryptedCredentialPrefDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private EditText usernameEt, passwordEt;

    EncryptedCredentialListener mListener;


    public static EncryptedCredentialPrefDialogFragmentCompat newInstance(String key) {
        EncryptedCredentialPrefDialogFragmentCompat encryptedCredentialPrefDialogFragmentCompat = new EncryptedCredentialPrefDialogFragmentCompat();
        Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        encryptedCredentialPrefDialogFragmentCompat.setArguments(b);
        return encryptedCredentialPrefDialogFragmentCompat;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean needInputMethod() {
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (EncryptedCredentialListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement EncryptedCredentialListener");
        }
    }

    @Override
    protected View onCreateDialogView(Context context) {
        View view = super.onCreateDialogView(context);


        usernameEt = view.findViewById(R.id.cred_username);
        passwordEt = view.findViewById(R.id.cred_password);

        return view;
    }




    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            Utils.makeToast(this.getContext(), "positive clicked");
            mListener.onDialogPositiveClick(getPreference(), usernameEt.getText().toString(), passwordEt.getText().toString());
        } else {
            mListener.onDialogNegativeClick(getPreference());
        }

    }

    public interface EncryptedCredentialListener {
        void onDialogPositiveClick(DialogPreference preference, String user, String pass);

        void onDialogNegativeClick(DialogPreference preference);
    }



}
