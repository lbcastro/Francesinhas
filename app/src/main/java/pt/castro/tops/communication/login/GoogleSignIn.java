package pt.castro.tops.communication.login;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import de.greenrobot.event.EventBus;
import pt.castro.tops.communication.UserEndpointActions;
import pt.castro.tops.events.EventBusHook;
import pt.castro.tops.events.user.NoUserEvent;
import pt.castro.tops.events.user.UserDataEvent;

/**
 * Created by lourenco on 26/03/16.
 */
public class GoogleSignIn implements GoogleApiClient.OnConnectionFailedListener {

    public static final int LOGIN_INDEX = 2;

    private static final int RC_SIGN_IN = 11;

    private boolean mSignedIn;
    private LoginObserver mObserver;

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInAccount mAccount;

    public GoogleSignIn(final FragmentActivity context) {
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(context).enableAutoManage(context /*
    FragmentActivity */, this /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API,
                mGoogleSignInOptions).build();
        mGoogleApiClient.connect();
    }

    public void setObserver(final LoginObserver observer) {
        mObserver = observer;
    }

    public void onActivityResult(final int requestCode, final Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void setSignInButton(final Activity activity, final SignInButton signInButton) {
        if (signInButton != null) {
            signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
            signInButton.setSize(SignInButton.SIZE_WIDE);
            signInButton.setScopes(mGoogleSignInOptions.getScopeArray());
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    activity.startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        EventBus.getDefault().register(this);
        if (result.isSuccess()) {
            mAccount = result.getSignInAccount();
            if (mAccount != null) {
                new UserEndpointActions(UserEndpointActions.GET_USER).execute(mAccount.getId());
                return;
            }
        }
        mObserver.onLoginFail();
        mSignedIn = false;
    }

    @EventBusHook
    public void onEvent(final UserDataEvent userDataEvent) {
        mSignedIn = true;
        mObserver.onLoginSuccess(LOGIN_INDEX, userDataEvent.getUserHolder());
        EventBus.getDefault().unregister(this);
    }

    @EventBusHook
    public void onEvent(final NoUserEvent event) {
        new UserEndpointActions(UserEndpointActions.ADD_USER).execute(mAccount.getId(), mAccount
                .getEmail());
    }

    public void googleSignIn() {
        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi
                .silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            handleSignInResult(pendingResult.get());
        } else {
            // There's no immediate result ready, displays some progress indicator and waits
            // for the
            // async callback.
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }

    public void signOut() {
        if (mSignedIn) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {

                @Override
                public void onResult(Status status) {
                    mSignedIn = false;
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mObserver.onLoginFail();
        mSignedIn = false;
    }
}
