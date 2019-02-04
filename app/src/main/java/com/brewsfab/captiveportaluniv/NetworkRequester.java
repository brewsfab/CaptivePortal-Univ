package com.brewsfab.captiveportaluniv;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkRequester {

    private static NetworkRequester instance;
    private RequestQueue mRequestQueue;
    private Context mContext;


    private NetworkRequester(Context context){
        this.mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkRequester getInstance(Context context){
        if(instance == null){
            instance = new NetworkRequester(context);
        }
        return instance;
    }


    public RequestQueue getRequestQueue() {
        if(mRequestQueue==null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
