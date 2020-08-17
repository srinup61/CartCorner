package com.intern.kartcorner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.Common;
import com.intern.kartcorner.ui.VerifyPhoneActivity;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "PhoneAuth";
    private EditText phoneText,countrycodeText;
    private Button sendButton;
    SpotsDialog waitingdilog;
    FirebaseAuth mAuth;
    private FirebaseAuth fbAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneText = (EditText) findViewById(R.id.phoneText);
        countrycodeText = (EditText) findViewById(R.id.countryCode);
        sendButton = (Button) findViewById(R.id.send_otp);
        mAuth=FirebaseAuth.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        waitingdilog=new SpotsDialog(LoginActivity.this);
    }
    public void sendCode(View view) {
        if(!Common.isInternetAvailable(LoginActivity.this)){
            Common.InternetError(LoginActivity.this);
            return;
        }
        String code = countrycodeText.getText().toString().trim();
        String number = phoneText.getText().toString().trim();
        if (number.isEmpty() || number.length() < 10) {
            phoneText.setError("Valid number is required");
            phoneText.requestFocus();
            return;
        }
        String phoneNumber = code + number;
        Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);

    }

    public void signOut(View view) {
        fbAuth.signOut();
        //  statusText.setText("Signed Out");
        //  signoutButton.setEnabled(false);
        sendButton.setEnabled(true);
    }

}

