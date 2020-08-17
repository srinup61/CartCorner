package com.intern.kartcorner.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.view.View;

import com.intern.kartcorner.R;
import com.intern.kartcorner.app.Constants;
import com.intern.kartcorner.helper.PrefManager;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThankyouActivity extends AppCompatActivity {
    @BindView(R.id.view)
    View view;
    private Session mSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);
        ButterKnife.bind(this);
        view.setAlpha((float) 0.8);
        show();
    }

    private void show() {

        new Handler().postDelayed(() -> {
            final Intent mainIntent = new Intent(ThankyouActivity.this, MainActivity .class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }, 5000);
    }
}
