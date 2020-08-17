package com.intern.kartcorner.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by Ravi on 08/07/15.
 */
public class PrefManager {
    // Shared Preferences
    private final SharedPreferences pref;

    // Editor for Shared preferences
    private final Editor editor;

    // Context
    private final Context _context;

    // Shared pref mode
    private final int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHive";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_CITY = "city";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_OTP = "OTP";
    private static final String KEY_NEW_MEM_OTP = "NEWMEMOTP";
    private static final String KEY_USER_ID = "ID";
    private static final String KEY_HOSTELWORKER_MOBILE = "hostelworkerNum";
    private static final String KEY_HOSTELUSER_MOBILE = "hosteluserNum";
    private static final String KEY_LOGIN_TYPE = "loginType";
    private static final String KEY_PROFILEPIC = "profilepic";
    private static final String KEY_USER_MSG_WAITING = "usermsgwait";
    private static final String KEY_INS_ID = "insid";
    private static final String KEY_WALLET_AMOUNT = "wallet";
    private static final String KEY_DEVICE_TOKEN = "devicetoken";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String KEY_CHECKIN_OTP = "checkinotp";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setCity(String cityName) {
        editor.putString(KEY_CITY, cityName);
        editor.commit();
    }

    public String getCity() {
        return pref.getString(KEY_CITY, null);
    }

    public void setWalletAmount(String amount) {
        editor.putString(KEY_WALLET_AMOUNT, amount);
        editor.commit();
    }

    public String getWalletAmount() {
        return pref.getString(KEY_WALLET_AMOUNT, null);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }



    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE_NUMBER, null);
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }


    public void setprofilepic(String profilepic) {
        editor.putString(KEY_PROFILEPIC, profilepic);
        editor.commit();
    }

    public String getprofilepic() {
        return pref.getString(KEY_PROFILEPIC, null);
    }

    public void setKeyHostelworkerMobile(String mobileNumber) {
        editor.putString(KEY_HOSTELWORKER_MOBILE, mobileNumber);
        editor.commit();
    }

    public String getKeyHostelworkerMobile() {
        return pref.getString(KEY_HOSTELWORKER_MOBILE, null);
    }

    public void setKeyHosteluserMobile(String mobileNumber) {
        editor.putString(KEY_HOSTELUSER_MOBILE, mobileNumber);
        editor.commit();
    }

    public String getKeyHosteluserMobile() {
        return pref.getString(KEY_HOSTELUSER_MOBILE, null);
    }
    public void setOTP(String otp) {
        editor.putString(KEY_OTP, otp);
        editor.commit();
    }

    public void setDeviceToken(String deviceToken) {
        editor.putString(KEY_DEVICE_TOKEN, deviceToken);
        editor.commit();
    }

    public String getDeviceToken() {
        return pref.getString(KEY_DEVICE_TOKEN, null);
    }

    public String getNewMemOTP() {
        return pref.getString(KEY_NEW_MEM_OTP, null);
    }

    public void setNewMemOTP(String otp) {
        editor.putString(KEY_NEW_MEM_OTP, otp);
        editor.commit();
    }

    public String getOTP() {
        return pref.getString(KEY_OTP, null);
    }

    public void setUserId(String otp) {
        editor.putString(KEY_USER_ID, otp);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public void setKeyLoginType(String logintype) {
        editor.putString(KEY_LOGIN_TYPE, logintype);
        editor.commit();
    }

    public String getKeyLoginType() {
        return pref.getString(KEY_LOGIN_TYPE, null);
    }

    public void setName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public String getName() {
        return pref.getString(KEY_NAME, null);
    }

    public void createLogin(String name,String mobile,String email,String secureid,String id) {
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_DEVICE_TOKEN, secureid);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        setName(name);
        setEmail(email);
        setMobileNumber(mobile);
        setUserId(id);
        setDeviceToken(secureid);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("name", pref.getString(KEY_NAME, null));
        profile.put("email", pref.getString(KEY_EMAIL, null));
        profile.put("mobile", pref.getString(KEY_MOBILE, null));
        return profile;
    }
}
