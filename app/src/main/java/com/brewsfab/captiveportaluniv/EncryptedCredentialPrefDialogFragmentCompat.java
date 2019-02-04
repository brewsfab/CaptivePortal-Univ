package com.brewsfab.captiveportaluniv;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.EditText;

import java.util.StringJoiner;

import static android.app.Activity.RESULT_OK;

public class EncryptedCredentialPrefDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private EditText usernameEt, passwordEt;
    private KeyguardManager keyguardManager;

    EncryptedCredentialListener mListener;



    public static EncryptedCredentialPrefDialogFragmentCompat newInstance(String key){
        EncryptedCredentialPrefDialogFragmentCompat encryptedCredentialPrefDialogFragmentCompat = new EncryptedCredentialPrefDialogFragmentCompat();
        Bundle b = new Bundle(1);
        b.putString(ARG_KEY,key);
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
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString()+" must implement EncryptedCredentialListener");
        }
    }

    @Override
    protected View onCreateDialogView(Context context) {
        View view = super.onCreateDialogView(context);


        usernameEt = view.findViewById(R.id.cred_username);
        passwordEt = view.findViewById(R.id.cred_password);

        return view;
    }


//    private void setPreferenceText(String text) {
//        DialogPreference preference = getPreference();
//        if (preference instanceof EncryptedCredentialPreference) {
//            EncryptedCredentialPreference encryptedCredentialPreference = ((EncryptedCredentialPreference) preference);
//            encryptedCredentialPreference.setText(text);
//            Utils.Log("encrypted preference");
//        }
//    }


    @Override
    public void onDialogClosed(boolean positiveResult) {
        //Todo remove placeholder before production
        if(positiveResult){
            Utils.makeToast(this.getContext(),"positive clicked");
//            Utils.Log(encryptedCredentials(usernameEt.getText().toString(),passwordEt.getText().toString()));
            mListener.onDialogPositiveClick(getPreference(), usernameEt.getText().toString(), passwordEt.getText().toString());
        }else{
//            Utils.makeToast(this.getContext(), "cancelled");
            mListener.onDialogNegativeClick(getPreference());
        }

    }

    public interface EncryptedCredentialListener{
//        void onDialogPositiveClick(DialogFragment dialogFragment, String user, String pass);
//        void onDialogNegativeClick(DialogFragment dialogFragment);

        void onDialogPositiveClick(DialogPreference preference, String user, String pass);
        void onDialogNegativeClick(DialogPreference preference);
    }

//    @Override
//    public void displayConfirmCredentials(int requestcode, String text) {
//        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null,null);
//        if(intent != null) {
//            intent.putExtra("user",text);
//            startActivityForResult(intent, requestcode);
//        }
//    }


}
