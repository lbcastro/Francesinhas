package pt.castro.tops.communication.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.tops.CustomApplication;
import pt.castro.tops.R;
import pt.castro.tops.communication.UserEndpointActions;
import pt.castro.tops.list.ListActivity;
import pt.castro.tops.tools.NotificationUtils;


/**
 * Created by lourenco.castro on 07/06/15.
 */
public class LoginActivity extends AppCompatActivity implements LoginObserver {

    private FacebookLogin mFacebookLogin;
    private GoogleSignIn mGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        setLayout();

        mGoogleSignIn = new GoogleSignIn(this);
        mGoogleSignIn.setObserver(this);
        mFacebookLogin = new FacebookLogin();
        mFacebookLogin.setObserver(this);
        mFacebookLogin.trackAccessToken(this);

        if (getIntent().getExtras() != null) {
            boolean logout = getIntent().getExtras().getBoolean("logout");
            if (logout) {
                mGoogleSignIn.signOut();
                LoginManager.getInstance().logOut();
                final SharedPreferences sharedPref = getSharedPreferences("TOPS_PREFERENCES",
                        Context.MODE_PRIVATE);
                sharedPref.edit().putInt("last_login", 0).apply();
                startLogin();
                return;
            }
        }

        final SharedPreferences sharedPref = getSharedPreferences("TOPS_PREFERENCES", Context
                .MODE_PRIVATE);
        final int loginIndex = sharedPref.getInt("last_login", 0);
        switch (loginIndex) {
            case 1:
                mFacebookLogin.login(this);
                break;
            case 2:
                mGoogleSignIn.googleSignIn();
                break;
            default:
                startLogin();
                break;
        }
    }

    @Override
    public void onLoginSuccess(final int sourceIndex, final UserHolder userHolder) {
        final SharedPreferences sharedPref = getSharedPreferences("TOPS_PREFERENCES", Context
                .MODE_PRIVATE);
        sharedPref.edit().putInt("last_login", sourceIndex).apply();
        CustomApplication.getUsersManager().setUser(userHolder);
        startList();
    }

    @Override
    public void onLoginFail() {
        NotificationUtils.toastLoginFailed(LoginActivity.this);
    }

    private void setLayout() {
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
    }

    private void startList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void startList(final String userId) {
        new UserEndpointActions(UserEndpointActions.GET_USER).execute(userId);


//        Intent intent = new Intent(this, ListActivity.class);
//        intent.putExtra("id", userId);
//        startActivity(intent);
//        this.finish();
    }

    private void startLogin() {
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        if (loginButton != null) {
            loginButton.setVisibility(View.VISIBLE);
            mFacebookLogin.setLoginButton(loginButton);
        }

        final View guestButton = findViewById(R.id.guest_button);
        if (guestButton != null) {
            guestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startList();
                }
            });
        }

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mGoogleSignIn.setSignInButton(this, signInButton);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGoogleSignIn.onActivityResult(requestCode, data);
        mFacebookLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFacebookLogin.stop();
    }
}
