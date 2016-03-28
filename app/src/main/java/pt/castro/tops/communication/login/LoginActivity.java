package pt.castro.tops.communication.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.tops.CustomApplication;
import pt.castro.tops.R;
import pt.castro.tops.list.ListActivity;
import pt.castro.tops.tools.AnimationUtils;
import pt.castro.tops.tools.NotificationUtils;


/**
 * Created by lourenco.castro on 07/06/15.
 */
public class LoginActivity extends AppCompatActivity implements LoginObserver {

    private View mLoginButtons;
    private ProgressWheel mProgressWheel;

    private FacebookLogin mFacebookLogin;
    private GoogleSignIn mGoogleSignIn;

    private AnimationSet mButtonsAnimations;
    private AnimationSet mProgressAnimations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        setLayout();
        setAnimations();

        mLoginButtons = findViewById(R.id.votes_parent);
        mProgressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        if (mProgressWheel != null) {
            mProgressWheel.setVisibility(View.VISIBLE);
            mProgressWheel.setBarColor(ContextCompat.getColor(this, R.color.blue_bright));
            mProgressWheel.spin();
        }

        mGoogleSignIn = new GoogleSignIn(this);
        mGoogleSignIn.setObserver(this);
        mFacebookLogin = new FacebookLogin();
        mFacebookLogin.setObserver(this);
        mFacebookLogin.trackAccessToken(this);

        if (getIntent().getExtras() != null) {
            boolean logout = getIntent().getExtras().getBoolean("logout");
            if (logout) {
                mGoogleSignIn.signOut();
                mFacebookLogin.signOut();
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

    private void setAnimations() {
        mProgressAnimations = AnimationUtils.getSlideBottomAnimation(100, 0, 0.0f, 1.0f, 200);
        mButtonsAnimations = AnimationUtils.getSlideBottomAnimation(0, 1000, 1.0f, 0.0f, 500);
    }

    private void setReverseAnimations() {
        mProgressAnimations = AnimationUtils.getSlideBottomAnimation(0, 100, 1.0f, 0.0f, 200);
        mButtonsAnimations = AnimationUtils.getSlideBottomAnimation(1000, 0, 0.0f, 1.0f, 500);
    }

    private void hideLoginButtons() {
        mProgressWheel.setVisibility(View.VISIBLE);
        mProgressWheel.startAnimation(mProgressAnimations);
        mProgressWheel.spin();
        mLoginButtons.startAnimation(mButtonsAnimations);
        setReverseAnimations();
    }

    private void showLoginButtons() {
        mProgressWheel.startAnimation(mProgressAnimations);
        mProgressWheel.stopSpinning();
        mProgressWheel.setVisibility(View.GONE);
        mLoginButtons.startAnimation(mButtonsAnimations);
        setAnimations();
    }

    @Override
    public void onLoginStart() {
        hideLoginButtons();
    }

    @Override
    public void onLoginSuccess(final int sourceIndex, final UserHolder userHolder) {
        final SharedPreferences sharedPref = getSharedPreferences("TOPS_PREFERENCES", Context
                .MODE_PRIVATE);
        sharedPref.edit().putInt("last_login", sourceIndex).apply();
        CustomApplication.getUsersManager().setUser(userHolder);
        setAnimations();
        startList();
    }

    @Override
    public void onLoginFail() {
        NotificationUtils.toastLoginFailed(LoginActivity.this);
        showLoginButtons();
    }

    private void setLayout() {
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

    private void startLogin() {
        final LoginButton facebookLoginButton = (LoginButton) findViewById(R.id
                .facebook_login_button);
        final TextView loginText = (TextView) findViewById(R.id.facebook_login_text);
        if (facebookLoginButton != null) {
            mFacebookLogin.setLoginButton(facebookLoginButton, loginText);
        }

        final SignInButton googleLoginButton = (SignInButton) findViewById(R.id
                .google_login_button);
        final TextView googleText = (TextView) findViewById(R.id.google_login_text);
        mGoogleSignIn.setSignInButton(this, googleLoginButton, googleText);

        final View guestButton = findViewById(R.id.guest_button);
        if (guestButton != null) {
            guestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startList();
                }
            });
        }
        mLoginButtons.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.GONE);

        final View rootView = findViewById(R.id.fragment_login_parent);
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GoogleSignIn.REQUEST_CODE) {
            mGoogleSignIn.onActivityResult(requestCode, data);
        } else {
            mFacebookLogin.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFacebookLogin.stop();
    }
}
