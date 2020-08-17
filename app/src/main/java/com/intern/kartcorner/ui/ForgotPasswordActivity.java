package com.intern.kartcorner.ui;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class ForgotPasswordActivity extends BaseActivity {
    @BindView(R.id.input_forgotmail)
    EditText inputforgotmail;
    private ShowToast showToast;
    private ProgressDailog progressDailog;
    @BindView(R.id.forgotview)
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        showToast = new ShowToast(this);
        progressDailog = new ProgressDailog(this);
        changeStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        hideToolbar();
        view.setAlpha(0.8f);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_forgot_password;
    }
    @OnClick(R.id.btn_forgot)
    void forgotmail(){
        progressDailog.showDailog();
        String mailid = inputforgotmail.getText().toString();
        if(isValidMail(mailid)){
            sendmail(mailid);
        }else {
            showToast.showWarningToast("Enter Valid Email Id");
            progressDailog.dismissDailog();
        }

    }

    private void sendmail(String mailid) {
        Ion.with(this)
                .load("POST",BASE_URL+"forgotpassword")
                .setMultipartParameter("email",mailid)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){
                            showToast.showWarningToast("Something went wrong");
                            progressDailog.dismissDailog();
                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String error = jsonObject.getString("error");
                                String msg = jsonObject.getString("message");
                                if(error.equals("true")){
                                    showToast.showWarningToast(msg);
                                }else {
                                    showToast.showSuccessToast(msg);
                                }
                                progressDailog.dismissDailog();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
    @OnClick(R.id.forgot_cancel)
    void onCreateAccountClick() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onCreateAccountClick();
    }
}
