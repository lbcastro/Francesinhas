package pt.castro.francesinhas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import icepick.Icepick;

/**
 * Created by lourenco.castro on 07/06/15.
 */
public class LoginActivity extends Activity {

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.fragment_login);
        startLogin();
        if (AccessToken.getCurrentAccessToken() != null) {
            startList();
        }
    }

    private void startList() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startLogin() {
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginManager", "Success");
                startList();
            }

            @Override
            public void onCancel() {
                Log.d("LoginManager", "Cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("LoginManager", "Error");
                e.printStackTrace();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
