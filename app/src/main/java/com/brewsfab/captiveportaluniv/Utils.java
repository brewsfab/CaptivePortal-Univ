package com.brewsfab.captiveportaluniv;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

class Utils {

    static void makeToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    static void Log(String message) {
        Log.i("pif", message);
    }

    static Bundle getCurrentWifiConnection(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        String wifiSSID = "";
        Network net = null;
        for (Network network : cm != null ? cm.getAllNetworks() : new Network[0]) {
            NetworkInfo networkInfo = cm != null ? cm.getNetworkInfo(network) : null;
            if (networkInfo != null) {
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
//                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI & networkInfo.isConnectedOrConnecting())) {
                    net = network;
                    wifiSSID = unQuote(networkInfo.getExtraInfo());
                }
            }
        }

        Bundle netInfo = new Bundle(2);
        netInfo.putString("wifiName", wifiSSID);
        Utils.Log("wifiName:  " + wifiSSID);
        netInfo.putParcelable("network", net);
        return netInfo;

    }

    private static String unQuote(String quotedString) {
        return quotedString.replace("\"", "");
    }

}
