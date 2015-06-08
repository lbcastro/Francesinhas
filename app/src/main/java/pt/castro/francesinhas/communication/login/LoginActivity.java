package pt.castro.francesinhas.communication.login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import icepick.Icepick;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.list.ListActivity;

/**
 * Created by lourenco.castro on 07/06/15.
 */
public class LoginActivity extends Activity {

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;

    private void getKey() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("pt.castro.francesinhas", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getKey();
        Icepick.restoreInstanceState(this, savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        trackAccessToken();
        if (AccessToken.getCurrentAccessToken() != null || Profile.getCurrentProfile() != null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            startList();
        } else {
            Log.d("LoginActivity", "No login found");
            setContentView(R.layout.fragment_login);
            View rootView = findViewById(R.id.fragment_login_parent);
            rootView.setVisibility(View.VISIBLE);
            mCallbackManager = CallbackManager.Factory.create();
            startLogin();
        }
    }

    private void startList() {
        Intent intent = new Intent(this, ListActivity.class);
        finish();
        startActivity(intent);
    }

    private void trackAccessToken() {
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                Log.d("LoginActivity", "Token has changed");
                if (currentAccessToken != null && !currentAccessToken.isExpired()) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                            Arrays.asList("public_profile"));
                    startList();
                }
            }
        };
    }

    private void startLogin() {
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                View rootView = findViewById(R.id.fragment_login_parent);
                rootView.setVisibility(View.GONE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAccessTokenTracker.stopTracking();
    }
}