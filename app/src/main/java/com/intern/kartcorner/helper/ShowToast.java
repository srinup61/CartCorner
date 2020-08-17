package com.intern.kartcorner.helper;

import android.content.Context;

import com.valdesekamdem.library.mdtoast.MDToast;

public class ShowToast {
    private Context context;
    public ShowToast(Context context){
        this.context = context;
    }
    public void showInfoToast(String msg) {
        MDToast mdToast = MDToast.makeText(context, msg);
        mdToast.show();
    }

    public void showSuccessToast(String msg) {
        MDToast.makeText(context, msg, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
    }

    public void showWarningToast(String msg) {
        MDToast.makeText(context, msg, MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
    }

    public void showErrorToast(String msg) {
        MDToast.makeText(context, msg, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
    }
}
