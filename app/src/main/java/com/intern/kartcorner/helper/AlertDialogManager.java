package com.intern.kartcorner.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.intern.kartcorner.R;

public class AlertDialogManager {
    public void showAlertDialog1(final Context context, String title, String message, final Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context,R.style.MyAlertDialogStyle).create();
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage("If you Want connect click below");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Mobile Data", (dialog, id) -> {
            context.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Wifi", (dialog, id) -> {

            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", (dialog, id) -> {
            ((Activity)context).finish();
            System.exit(0);
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public void showAlertDialog(final Context context, String title, String message, final Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context,R.style.MyAlertDialogStyle).create();
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, id) -> {
           dialog.dismiss();
        });
        // Showing Alert Message
        alertDialog.show();
    }


}
