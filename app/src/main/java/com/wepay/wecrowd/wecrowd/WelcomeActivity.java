package com.wepay.wecrowd.wecrowd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import internal.LoginManager;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @SuppressWarnings("unused")
    public void didChooseLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @SuppressWarnings("unused")
    public void didChooseBrowse(View view) {
        LoginManager.logout();
        startActivity(new Intent(this, CampaignFeedActivity.class));
    }
}
