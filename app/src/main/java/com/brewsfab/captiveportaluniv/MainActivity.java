package com.brewsfab.captiveportaluniv;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CryptoHelper.CredentialNeededListener {

    private static final int WALL_GARDEN_STATUS_CODE = 302;
    private KeyguardManager keyguardManager;
    private String encryptedCred;
    private TextView tv;

    public static final String URL = "http://clients3.google.com/generate_204";
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        CryptoHelper.init(keyguardManager); //Init the crypto helper
        CryptoHelper.setmCredentialNeededListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        tv = findViewById(R.id.tv_show);
    }

    public void goToSecond(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public void decryptCredentials() {

        Utils.Log("start decrypt");

        //Get the Preference
        encryptedCred = sharedPreferences.getString("credentials", null);
        if (encryptedCred != null) { //if credentials exist
            CryptoHelper.decryptText(encryptedCred);
        } else {
            Utils.makeToast(this, "Please save your credentials");
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.Log("relaunched decryption");
        if (requestCode == CryptoHelper.DECRYPT_CREDENTIALS) {
            if (resultCode == RESULT_OK) {
                //TODO decrypt the text
                Utils.Log("restarted decryption");
                CryptoHelper.decryptText(encryptedCred);
            } else {
                Utils.Log("failed to decrypt");
                //TODO dismiss the decryption process
            }
        }
    }

    @Override
    public void displayConfirmCredentials(int requestCode) {
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
        if (intent != null) {
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void successEncryption(String encryptedText) {

    }

    @Override
    public void successDecryption(String decryptedText) {
//        Utils.Log("The DECRYPTED: "+decryptedText);
        startConnection(decryptedText);

    }



    public void checkNetworkWall(View view) {
        getNetworkResponse(Utils.getCurrentWifiConnection(view.getContext()));
    }


    private void getNetworkResponse(Bundle currentWifiConnection) {

        String activeWifiName = currentWifiConnection.getString("wifiName");
        Network activeNetwork = currentWifiConnection.getParcelable("network");

        String preferredNetork = sharedPreferences.getString("preferred_network", null);

        if (activeWifiName != null && activeWifiName.equals(preferredNetork)) {//Preferred network detected, check the network
            Utils.Log("Preferred network detected, start checking");

            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cm.bindProcessToNetwork(activeNetwork);
                } else {
                    ConnectivityManager.setProcessDefaultNetwork(activeNetwork);
                }

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("def-response", "" + response);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("def-error", "" + error.networkResponse.statusCode);
                        // 302 treated as error, let's assume it wall garden
                        if (error.networkResponse.statusCode == WALL_GARDEN_STATUS_CODE) {
                            Log.i("def-error", "Captive portal detected");
                            decryptCredentials();
                        }


                    }
                }) {
                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        int statusCode = response.statusCode;
                        Log.i("def", "" + statusCode);
                        if (statusCode != 204) {
//                            startCaptivePortalNotification();
                            Utils.Log("IS WALL");
                        } else {
                            Utils.Log("ALready connnected");
                        }
                        return super.parseNetworkResponse(response);
                    }
                };

                NetworkRequester.getInstance(this).addToRequestQueue(stringRequest);
            }

        }


    }

    private void startConnection(final String credentials) {

        Utils.Log("Dans startconnection()");
        String url = "http://httpbin.org/post";
//        String url = "https://aruba151.naist.jp/cgi-bin/login";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String[] id = credentials.split(":", 2);
                params.put("user", id[0]);
                params.put("password", id[1]);

                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Utils.Log("Parsed responseCode: " + response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };
        NetworkRequester.getInstance(this).addToRequestQueue(postRequest);

    }
}
