package pt.castro.tops.communication.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import pt.castro.tops.R;
import pt.castro.tops.list.ListActivity;
import pt.castro.tops.tools.NotificationUtils;

/**
 * Created by lourenco.castro on 07/06/15.
 */
public class LoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;

    private void getKey() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("pt.castro.tops", PackageManager
                    .GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
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
        setContentView(R.layout.fragment_login);
        final View rootView = findViewById(R.id.fragment_login_parent);
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
        }

        final TextView textView = (TextView) findViewById(R.id.title);
        final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue Bold.otf");
        if (textView != null) {
            textView.setTypeface(typeFace);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        trackAccessToken();
        mCallbackManager = CallbackManager.Factory.create();
        if (AccessToken.getCurrentAccessToken() != null || Profile.getCurrentProfile() != null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList
                    ("public_profile"));
            startList();
        } else {
            startLogin();
        }
    }

    private void startList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void trackAccessToken() {
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken
                    currentAccessToken) {
                Log.d("LoginActivity", "Token has changed");
                if (currentAccessToken != null && !currentAccessToken.isExpired()) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity
                            .this, Collections.singletonList("public_profile"));
                    startList();
                }
            }
        };
    }

    private void startLogin() {
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.setVisibility(View.VISIBLE);
        LoginManager.getInstance().registerCallback(mCallbackManager, new
                FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginManager", "Success");
                startList();
            }

            @Override
            public void onCancel() {
                Log.d("LoginManager", "Cancel");
                NotificationUtils.toastLoginFailed(LoginActivity.this);
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("LoginManager", "Error");
                e.printStackTrace();
                NotificationUtils.toastLoginFailed(LoginActivity.this);
            }
        });
        final View guestButton = findViewById(R.id.guest_button);
        if (guestButton != null) {
            guestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startList();
                }
            });
        }
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
