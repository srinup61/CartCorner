package com.intern.kartcorner.helper;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDailog {
    private ProgressDialog pDialog;
    private Context context;
    public ProgressDailog(Context context){
        this.context = context;
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait....");
        pDialog.setCancelable(false);
    }
    public void showDailog(){
        pDialog.show();
    }
    public void dismissDailog(){
        pDialog.dismiss();
    }
}
