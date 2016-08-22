package com.ryletech.supaorders;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.pixplicity.easyprefs.library.Prefs;

import static com.ryletech.supaorders.util.AppConfig.TAG;

public class AppController extends Application {

    private RequestQueue mRequestQueue;

    private static AppController mInstance;
//    String defaultFilePath;
//    private static Storage storage;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

//        defaultFilePath = String.valueOf(getExternalFilesDir(null));

        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(TAG)
                .setUseDefaultSharedPreference(true)
                .build();

//        Initialize Location, Tuk
//        Prefs.putDouble(LATITUDE,0);
//        Prefs.putDouble(LONGITUDE,0);

//        initializeStorage();




    }

//    public static Storage getStorage() {
//        return storage;
//    }

//    private void initializeStorage() {
//        if (SimpleStorage.isExternalStorageWritable()) {
//            storage = SimpleStorage.getExternalStorage();
//        } else {
//            storage = SimpleStorage.getInternalStorage(this);
//        }
//
//        //create the external file directory
//        if (!storage.isDirectoryExists(defaultFilePath)) {
//            storage.createDirectory(defaultFilePath);
//            Log.i(TAG, "initializeStorage: External file directory created");
//        }
//
////        create the json file for offline Designs data
//        if(!storage.isFileExist(defaultFilePath, FILENAME_NEARBY_PLACES)) {
//            storage.createFile(defaultFilePath,FILENAME_NEARBY_PLACES,"");
//            Log.i(TAG, "initializeStorage: The designs.txt file path is: " + storage.getFile(defaultFilePath, FILENAME_NEARBY_PLACES));
//        }
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
