package com.intern.kartcorner.helper;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;

import androidx.multidex.MultiDex;

import com.intern.kartcorner.app.ConnectivityReceiver;

public class CustomApplication extends Application {
    private static CustomApplication mInstance;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static synchronized CustomApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(500);
        mInstance = this;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
