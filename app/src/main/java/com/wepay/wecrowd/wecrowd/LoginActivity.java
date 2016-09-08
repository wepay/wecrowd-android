package com.wepay.wecrowd.wecrowd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import internal.APIResponseHandler;
import internal.AppNotifier;
import internal.InputManager;
import internal.LoginManager;
import models.User;

public class LoginActivity extends AppCompatActivity {
    private EditText entryCredentials;
    private EditText entryPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        entryCredentials = (EditText) findViewById(R.id.edit_text_credentials);
        entryPassword = (EditText) findViewById(R.id.edit_text_password);

        entryCredentials.setText(R.string.demo_email);
        entryPassword.setText(R.string.demo_password);

        InputManager.setKeyboardDismissForEditTexts(this,
                new EditText[] {entryCredentials, entryPassword});
    }

    @SuppressWarnings("unused")
    public void didRequestLogin(View view) {
        String textCredentials, textPassword;

        textCredentials = entryCredentials.getText().toString();
        textPassword = entryPassword.getText().toString();

        LoginManager.login(textCredentials, textPassword, new APIResponseHandler() {
            @Override
            public void onCompletion(User user, Throwable throwable) {
                super.onCompletion(user, throwable);

                if (throwable == null) {
                    beginNextActivity();
                } else {
                    AppNotifier.showSimpleError(LoginActivity.this,
                            getString(R.string.error_login_title),
                            getString(R.string.error_login_preface),
                            throwable.getLocalizedMessage());
                }
            }
        });
    }

    private void beginNextActivity() {
        startActivity(new Intent(this, CampaignFeedActivity.class));
    }
}
