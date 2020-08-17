package com.intern.kartcorner.app;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static PrefManager instance;
    private final SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public PrefManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences("paytmgateway", Context.MODE_PRIVATE);
    }

    public static PrefManager with(Context context) {
        if (instance == null) {
            instance = new PrefManager(context);
        }
        return instance;
    }

}
