package com.alliejc.wcstinder;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by acaldwell on 3/26/18.
 */

public class LoginDialog extends DialogFragment{
    private LoginButton mLoginButton;
    private TextView mLoginText;


    private CallbackManager mCallbackManager;
    private static final String EMAIL = "email";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_login, container, false);
        mLoginButton = (LoginButton) root.findViewById(R.id.login_button);
        mLoginText = (TextView) root.findViewById(R.id.login_text);

        mLoginButton.setFragment(this);
        mLoginButton.setReadPermissions(Arrays.asList(EMAIL));

        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mLoginText.setText("User ID:  " +
                        loginResult.getAccessToken().getUserId() + "\n" +
                        "Auth Token: " + loginResult.getAccessToken().getToken());

                if(isFacebookAppInstalled()){
                    Toast.makeText(getApplicationContext(), "installed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "not installed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancel() {
                mLoginText.setText("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException error) {
                mLoginText.setText("Login attempt failed.");
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isFacebookAppInstalled() {
        try {
            getApplicationContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
