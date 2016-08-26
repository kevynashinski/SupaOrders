package com.ryletech.supaorders.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by kevynashinski on 11/9/2015.
 */
public class InternetConnection {

    private Context context;

    public InternetConnection(Context context) {
        this.context = context;
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

//        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
