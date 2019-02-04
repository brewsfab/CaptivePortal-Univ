package com.brewsfab.captiveportaluniv;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    //Keys
    private static final String KEY_PREFERRED_NET = "preferred_network";

    //Prefs
    private Preference pref_preferred_net;


    //Prefs change listener
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_pref,rootKey);


        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                pref_preferred_net.setSummary(sharedPreferences.getString(KEY_PREFERRED_NET,"not set"));
            }
        };

        pref_preferred_net = getPreferenceManager().findPreference(KEY_PREFERRED_NET);


        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

        prefs.registerOnSharedPreferenceChangeListener(mListener);

        mListener.onSharedPreferenceChanged(prefs, KEY_PREFERRED_NET);


    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {

        DialogFragment dialogFragment = null;

        if(preference instanceof EncryptedCredentialPreference){
            dialogFragment = EncryptedCredentialPrefDialogFragmentCompat.newInstance(preference.getKey());
        }

        if(preference instanceof PrefWifiEditTextPreference){
            dialogFragment = PrefWifiEditTextPreferenceDialogFragmentCompat.newInstance(preference.getKey());

        }

        if(dialogFragment!=null){
            dialogFragment.setTargetFragment(this,0);
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference." +
                    "preferenceFragment.DIALOG");
        }else{
        super.onDisplayPreferenceDialog(preference);

        }


    }


}
