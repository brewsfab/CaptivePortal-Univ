package com.brewsfab.captiveportaluniv;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

public class PrefWifiEditTextPreference extends DialogPreference {

    private String mText;


    private int mPrefWifiEditTextPreferenceId = R.layout.custom_prefwifi_preference;

    public PrefWifiEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PrefWifiEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public PrefWifiEditTextPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setPersistent(true);
    }

    public PrefWifiEditTextPreference(Context context) {
        this(context, null);
    }

    @Override
    public int getDialogLayoutResource() {
        return mPrefWifiEditTextPreferenceId;
    }



    public String getText() {
        return mText;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setText(restorePersistedValue ? getPersistedString(mText) : (String) defaultValue);
    }

    public void setText(String text) {
        mText = text;
        persistString(text);
    }
}
