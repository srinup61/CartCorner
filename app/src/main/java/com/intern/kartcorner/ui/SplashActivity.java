package com.intern.kartcorner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.content.ContextCompat;

import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.AlertDialogManager;
import com.intern.kartcorner.helper.PrefManager;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    private PrefManager prefManager;
    private AlertDialogManager alertDialogManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        changeStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        hideToolbar();
        alertDialogManager = new AlertDialogManager();
        prefManager = new PrefManager(this);

        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(() -> {

                if(prefManager.isLoggedIn()){
                    launchHomeScreen();
                    }
                else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

        }, SPLASH_TIME_OUT);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    /**
     * Fetching app configuration from server
     * This will get PayTM configuration required for payment related operations
     */

    private void launchHomeScreen() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
