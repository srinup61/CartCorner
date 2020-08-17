package com.intern.kartcorner.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.PrefManager;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class VerifyPhoneActivity extends AppCompatActivity {
    private String verificationId;
    private FirebaseAuth mAuth;
    private SpotsDialog waitingdilog;
    TextInputEditText editText;
    AppCompatButton buttonSignIn;
    PrefManager prefManager;
    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();
        waitingdilog = new SpotsDialog(this);
        prefManager = new PrefManager(this);
        editText = findViewById(R.id.editTextCode);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        sendVerificationCode(phoneNumber);
    }
    private void verifyCode(String code) {
        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        }catch (IllegalStateException e){
            e.printStackTrace();
        }

    }
    public void verifyCode(View view){
        String code = editText.getText().toString().trim();

        if (code.isEmpty() || code.length() < 6) {

            editText.setError("Enter code...");
            editText.requestFocus();
            return;
        }
        verifyCode(code);
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        waitingdilog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(prefManager.isFirstTimeLaunch()){
                            prefManager.setMobileNumber(phoneNumber);
                            Intent intent = new Intent(VerifyPhoneActivity.this, RegisterActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else {
                            prefManager.setMobileNumber(phoneNumber);
                            Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } else {
                        waitingdilog.dismiss();
                    }
                });
    }

    private void sendVerificationCode(String number) {
        waitingdilog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

        waitingdilog.dismiss();
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            waitingdilog.dismiss();
        }
    };
}
