package com.brewsfab.captiveportaluniv;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PrefWifiEditTextPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private EditText prefWifi_ed;


    public static PrefWifiEditTextPreferenceDialogFragmentCompat newInstance(String key){
        PrefWifiEditTextPreferenceDialogFragmentCompat fragmentCompat =new PrefWifiEditTextPreferenceDialogFragmentCompat();
        Bundle b = new Bundle(1);
        b.putString(ARG_KEY,key);
        fragmentCompat.setArguments(b);
        return fragmentCompat;
    }



    @Override
    protected View onCreateDialogView(Context context) {
        Utils.Log("In the on create view ");

        View view = super.onCreateDialogView(context);
        prefWifi_ed = view.findViewById(R.id.preferred_wifi_ed);

        PrefWifiEditTextPreference prefWifiEditTextPreference = ((PrefWifiEditTextPreference) getPreference());
        prefWifi_ed.setText(prefWifiEditTextPreference.getText());


        Button detect_btn = view.findViewById(R.id.btn_detect);

        Bundle connectionInfoBundle = Utils.getCurrentWifiConnection(context);
        final String activeWifiName = connectionInfoBundle!=null?connectionInfoBundle.getString("wifiName"):"";
        Utils.Log("active wifi= "+activeWifiName);

        if(!(activeWifiName != null && activeWifiName.isEmpty())){
            detect_btn.setVisibility(View.VISIBLE);
            detect_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"Detect clicked",Toast.LENGTH_LONG).show();
                    prefWifi_ed.setText(activeWifiName);
                }
            });


        }else{
            detect_btn.setVisibility(View.GONE);
        }

        return view;
    }




    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

    }


    /**
     *
     * Force to display the soft keyboard
     */
    @Override
    protected boolean needInputMethod() {
        return true;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if(positiveResult){
            DialogPreference preference = getPreference();
            if(preference instanceof PrefWifiEditTextPreference){
                PrefWifiEditTextPreference prefWifiEditTextPreference = ((PrefWifiEditTextPreference) preference);
                prefWifiEditTextPreference.setText(prefWifi_ed.getText().toString());
            }
        }

    }
}
