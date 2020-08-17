package com.intern.kartcorner.helper;

import android.content.Context;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.intern.kartcorner.app.Constants.BASE_URL;

/**
 * Created by JNTUH on 15-02-2018.
 */

public class SetBadgeCount {
    public static String k;
    public PrefManager prefManager;
    public String uid;
    public Context context;
    public SetBadgeCount(Context context) {
        this.context = context;
    }
    public void badgecount(){
        prefManager = new PrefManager(context);
        Ion.with(context)
                .load("POST",BASE_URL+"getcartitems")
                .setMultipartParameter("userid",prefManager.getUserId())
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){

                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("cartitems");
                            k = String.valueOf(jsonArray.length());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }
}
