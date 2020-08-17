package com.intern.kartcorner.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.LocationAddress;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ShowToast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import dmax.dialog.SpotsDialog;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.intern.kartcorner.app.Constants.BASE_URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText name,email,mobileno,address,landmark,pincode;
    private Button update;
    private SpotsDialog spotsDialog;
    private PrefManager prefManager;
    private FirebaseAuth firebaseAuth;
    private ShowToast showToast;
    private FusedLocationProviderClient client;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String userpinCode,userCity,lattitude,longtude,userAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        showToast = new ShowToast(this);

        spotsDialog = new SpotsDialog(this);
        prefManager = new PrefManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        name=findViewById(R.id.etName);
        email=findViewById(R.id.etEmail);
        mobileno=findViewById(R.id.etMobile);
        mobileno.setText(prefManager.getMobileNumber().replace("+91",""));
        //mobileno.setText("880");
        address=findViewById(R.id.etAddress);
        landmark=findViewById(R.id.etlandmark);
        pincode=findViewById(R.id.pincod);
        update =findViewById(R.id.upload);
        client = LocationServices.getFusedLocationProviderClient(this);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert locationManager != null;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            getprofile(prefManager.getMobileNumber());
        }else{
            showSettingsAlert();
        }
    }
    public void updateprofile(View view){
        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userMobile = mobileno.getText().toString();
        String userAddress = address.getText().toString();
        String landMark = landmark.getText().toString();
        String pinCode = pincode.getText().toString();
        String firebaseToken = firebaseAuth.getCurrentUser().getUid();
        String secureId = "";
        String latlong = lattitude+","+longtude;
        String userid = "CACO"+ gen();
        if(userName.isEmpty()){
            name.setError("Enter Your Name");
            return;
        }
        if(userEmail.isEmpty()){
            name.setError("Enter Email");
            return;
        }
        if(userMobile.isEmpty()){
            name.setError("Enter Mobile Number");
            return;
        }
        if(userAddress.isEmpty()){
            name.setError("Enter Address");
            return;
        }
        if(pinCode.isEmpty()){
            name.setError("Enter Pincode");
            return;
        }
        updateprofile(userid,userName,userMobile,userEmail,userAddress,landMark,pinCode,firebaseToken,secureId,latlong);
    }
    private int gen() {
        Random r = new Random( System.currentTimeMillis());
        return 10000 + r.nextInt(20000);
    }
    private void updateprofile(String userId,String userName, String userMobile, String userEmail, String userAddress, String userCity, String pinCode, String firebaseToken, String secureId, String latlong) {
        spotsDialog.show();
        Ion.with(RegisterActivity.this)
                .load("POST",BASE_URL+"signup")
                .setMultipartParameter("userid",userId)
                .setMultipartParameter("username",userName)
                .setMultipartParameter("userphone",userMobile)
                .setMultipartParameter("usermail",userEmail)
                .setMultipartParameter("userdefaultaddress",userAddress)
                .setMultipartParameter("city",userCity)
                .setMultipartParameter("latlong",latlong)
                .setMultipartParameter("secureid",secureId)
                .setMultipartParameter("firebasetoken",firebaseToken)
                .setMultipartParameter("password","")
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        spotsDialog.dismiss();
                        showToast.showErrorToast("Check Your Internet Connection");
                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String error = jsonObject.getString("error");
                            String msg = jsonObject.getString("message");
                            if(error.equals("true")){
                                showToast.showErrorToast(msg);
                            }
                            if(error.equals("false")){
                                showToast.showSuccessToast(msg);
                                prefManager.setFirstTimeLaunch(false);
                                prefManager.createLogin(userName,userMobile,userEmail,secureId,userId);
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                            spotsDialog.dismiss();
                        } catch (JSONException e1) {
                            spotsDialog.dismiss();
                            e1.printStackTrace();
                        }
                    }
                });
    }
    public void checkLocationPermission() {
        Dexter.withActivity(RegisterActivity.this)
                .withPermission(ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        getLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("Confirmation !")
                                .setMessage("Please Allow to getting current location details")
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(RegisterActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_LOCATION);
                                })
                                .create()
                                .show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        getLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
    private void getLocation() {
        client.getLastLocation().addOnSuccessListener(RegisterActivity.this, location -> {
            if (location != null) {
                lattitude = String.valueOf(location.getLatitude());
                longtude = String.valueOf(location.getLongitude());
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(location.getLatitude(),location.getLongitude(),RegisterActivity.this,new GeocoderHandler());
            }
        });
    }
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    userAddress = bundle.getString("address");
                    userpinCode = bundle.getString("pincode");
                    userCity = bundle.getString("city");
                    prefManager.setCity(userCity);
                    address.setText(userAddress);
                    pincode.setText(userpinCode);
                    landmark.setText(userCity);
                    break;
                default:
                    locationAddress = null;
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getprofile(String mobileNumber) {
        spotsDialog.show();
        Ion.with(this)
                .load("POST",BASE_URL+"verifyprofile")
                .setMultipartParameter("userphone",mobileNumber.replace("+91",""))
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        showToast.showWarningToast("Something went wrong");
                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String msg = jsonObject.getString("inserted");
                            if(msg.equals("1")){
                                String userName = jsonObject.getString("username");
                                String userMobile = jsonObject.getString("userphone");
                                String userEmail = jsonObject.getString("usermail");
                                String secureId = jsonObject.getString("secureid");
                                String userId = jsonObject.getString("userid");
                                String userCity = jsonObject.getString("userCity");
                                prefManager.setCity(userCity);
                                prefManager.setFirstTimeLaunch(false);
                                prefManager.createLogin(userName,userMobile,userEmail,secureId,userId);
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                spotsDialog.dismiss();
                            }else {
                                checkLocationPermission();
                            }
                        } catch (JSONException e1) {
                            checkLocationPermission();
                            e1.printStackTrace();
                        }
                    }
                });
    }
}
