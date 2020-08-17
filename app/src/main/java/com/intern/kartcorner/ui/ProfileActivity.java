package com.intern.kartcorner.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.profile_username)
    TextView profilename;
    @BindView(R.id.profile_usermobile)
    TextView profilmobile;
    @BindView(R.id.profile_useremail)
    TextView profilemail;
    @BindView(R.id.profile_address)
    TextView profileaddress;
    @BindView(R.id.profile_picture)
    CircleImageView profilepic;
    @BindView(R.id.walletamount)
    TextView walletamount;
    private PrefManager prefManager;
    private ProgressDailog progressDailog;
    private ShowToast showToast;
    private String profileName,profilEmail,profileMobile,userid;
    private AlertDialog alertDialogAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        ActionBar actionBar;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        prefManager = new PrefManager(this);
        progressDailog = new ProgressDailog(this);
        showToast = new ShowToast(this);
        userid = prefManager.getUserId();
        walletamount.setText(prefManager.getWalletAmount());
        loadProfile(userid);
    }

    private void loadProfile(String userId) {
        progressDailog.showDailog();
        Ion.with(this)
                .load("POST",BASE_URL+"userprofile")
                .setMultipartParameter("userid",userId)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        progressDailog.dismissDailog();
                        showToast.showErrorToast("Something Went Wrong");
                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("userdata");
                            profileName = jsonObject1.getString("username");
                            profilename.setText(profileName);
                            prefManager.setName(profileName);
                            profilEmail = jsonObject1.getString("usermail");
                            profilemail.setText(profilEmail);
                            prefManager.setEmail(profilEmail);
                            profileMobile = jsonObject1.getString("userphone");
                            profilmobile.setText("+91 "+profileMobile);
                            prefManager.setMobileNumber(profileMobile);
                            profileaddress.setText(jsonObject1.getString("userdefaultaddress"));
                            String url = null;
                            if(jsonObject1.has("userprofilepic")){
                                url = "http://freshcart.info/Freshkart/Api/"+jsonObject1.getString("userprofilepic");
                                Picasso.with(ProfileActivity.this).load(url).fit().centerCrop().into(profilepic);
                                prefManager.setprofilepic(url);
                            }
                            progressDailog.dismissDailog();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }
    @OnClick(R.id.profile_editprofile)
    void editprofile(){
        Intent intent = new Intent(ProfileActivity.this,UpdateProfileActivity.class);
        intent.putExtra("name",profileName);
        intent.putExtra("phone",profileMobile);
        intent.putExtra("email",profilEmail);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.profile_logout)
    void logout(){
        exitByBackKey();
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
                .setMessage("Do you want to Confirm the Logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        prefManager.clearSession();
                        Intent logout = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(logout);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                })
                .show();

    }

//    @OnClick(R.id.profile_updatepassword)
//    void profileupdatepassword(){
//        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
//        View mView = layoutInflaterAndroid.inflate(R.layout.updatepassword, null);
//        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);
//        alertDialogBuilderUserInput.setView(mView);
//
//        final EditText oldpassword = mView.findViewById(R.id.profiloldpassword);
//        final EditText newpassword = mView.findViewById(R.id.profilnewpassword);
//        final EditText newconfpassword = mView.findViewById(R.id.profilnewconfpassword);
//        final AppCompatButton cancel = mView.findViewById(R.id.profile_update_cancel);
//        final AppCompatButton update = mView.findViewById(R.id.profile_update_password);
//
//        cancel.setOnClickListener(view -> alertDialogAndroid.dismiss());
//        update.setOnClickListener(view -> {
//            progressDailog.showDailog();
//            String oldpass = oldpassword.getText().toString();
//            String newpass = newpassword.getText().toString();
//            String confpass = newconfpassword.getText().toString();
//
//            if(oldpass.isEmpty()||newpass.isEmpty()||confpass.isEmpty()){
//                showToast.showWarningToast("All fields are mandatory");
//                progressDailog.dismissDailog();
//                return;
//            }if(!newpass.equals(confpass)){
//                progressDailog.dismissDailog();
//                showToast.showWarningToast("New password should be match with confirm password");
//            }else {
//                updatepassword(oldpass,newpass);
//            }
//        });
//        alertDialogAndroid = alertDialogBuilderUserInput.create();
//        alertDialogAndroid.show();
//    }

//    private void updatepassword(String oldpass, String newpass) {
//        Ion.with(ProfileActivity.this)
//                .load("POST",BASE_URL+"updatepassword")
//                .setMultipartParameter("userid",userid)
//                .setMultipartParameter("oldpassword",oldpass)
//                .setMultipartParameter("newpassword",newpass)
//                .asString()
//                .setCallback(new FutureCallback<String>() {
//                    @Override
//                    public void onCompleted(Exception e, String result) {
//                        if(e!=null){
//
//                        }else {
//                            try {
//                                JSONObject jsonObject = new JSONObject(result);
//                                String error = jsonObject.getString("error");
//                                String mesg = jsonObject.getString("message");
//                                if(error.equals("false")){
//                                    progressDailog.dismissDailog();
//                                    alertDialogAndroid.dismiss();
//                                    showToast.showSuccessToast(mesg);
//                                }else {
//                                    progressDailog.dismissDailog();
//                                    showToast.showWarningToast(mesg);
//                                }
//
//                            } catch (JSONException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }
//                });
//
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
