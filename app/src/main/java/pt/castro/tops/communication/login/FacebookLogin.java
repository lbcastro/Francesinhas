package pt.castro.tops.communication.login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import de.greenrobot.event.EventBus;
import pt.castro.tops.communication.UserEndpointActions;
import pt.castro.tops.events.EventBusHook;
import pt.castro.tops.events.user.NoUserEvent;
import pt.castro.tops.events.user.UserDataEvent;

/**
 * Created by lourenco on 26/03/16.
 */
public class FacebookLogin {

    public static final int LOGIN_INDEX = 1;

    private boolean mSignedIn;
    private LoginObserver mObserver;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;

    public FacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
    }

    public void setObserver(final LoginObserver observer) {
        mObserver = observer;
    }

    public String login(final Activity activity) {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null) {
            LoginManager.getInstance().logInWithReadPermissions(activity, Collections
                    .singletonList("public_profile"));
            return token.getUserId();
        }

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            LoginManager.getInstance().logInWithReadPermissions(activity, Collections
                    .singletonList("public_profile"));
            return profile.getId();
        }
        return null;
    }

    public void trackAccessToken(final Activity activity) {
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken
                    currentAccessToken) {
                if (currentAccessToken != null && !currentAccessToken.isExpired()) {
                    LoginManager.getInstance().logInWithReadPermissions(activity, Collections
                            .singletonList("public_profile"));
                    handleSignInResult(currentAccessToken);
                }
            }
        };
    }

    private void handleSignInResult(final AccessToken accessToken) {
        EventBus.getDefault().register(this);
        new UserEndpointActions(UserEndpointActions.GET_USER).execute(accessToken.getUserId());
    }

    @EventBusHook
    private void onEvent(final UserDataEvent event) {
        mObserver.onLoginSuccess(LOGIN_INDEX, event.getUserHolder());
        mSignedIn = true;
        EventBus.getDefault().unregister(this);
    }

    @EventBusHook
    private void onEvent(final NoUserEvent event) {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new
                GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.i("LoginActivity", response.toString());
                // Get facebook data from login
                Bundle bFacebookData = getFacebookData(object);
                if (bFacebookData == null) {
                    mObserver.onLoginFail();
                    return;
                }
                new UserEndpointActions(UserEndpointActions.ADD_USER).execute(AccessToken
                        .getCurrentAccessToken().getUserId(), bFacebookData.getString("email"));
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, " +
                "location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private Bundle getFacebookData(JSONObject object) {
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id +
                        "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email")) bundle.putString("email", object.getString("email"));
            if (object.has("gender")) bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday")) bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException ignored) {

        }
        return null;
    }

    public void setLoginButton(final LoginButton loginButton) {
        loginButton.setReadPermissions("public_profile");
        LoginManager.getInstance().registerCallback(mCallbackManager, new
                FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleSignInResult(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                mObserver.onLoginFail();
                mSignedIn = false;
            }

            @Override
            public void onError(FacebookException e) {
                mObserver.onLoginFail();
                mSignedIn = false;
            }
        });
    }

    public void stop() {
        mAccessTokenTracker.stopTracking();
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getKey(final Activity activity) {
        PackageInfo info;
        try {
            info = activity.getPackageManager().getPackageInfo("pt.castro.tops", PackageManager
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
}
