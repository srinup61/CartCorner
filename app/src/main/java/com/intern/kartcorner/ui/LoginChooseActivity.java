package com.intern.kartcorner.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.intern.kartcorner.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginChooseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choose);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.loginchoose_login)
    void login(){
        Intent intent = new Intent(LoginChooseActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.loginchoose_register)
    void register(){
        Intent intent = new Intent(LoginChooseActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.guest_user)
    void guestuser(){
        Intent intent = new Intent(LoginChooseActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
