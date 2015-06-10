package pt.castro.francesinhas.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.backend.myApi.model.JsonMap;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.francesinhas.communication.EndpointGetItems;
import pt.castro.francesinhas.communication.EndpointUserVote;
import pt.castro.francesinhas.communication.EndpointsAsyncTask;
import pt.castro.francesinhas.communication.UserEndpointActions;
import pt.castro.francesinhas.communication.login.LoginActivity;
import pt.castro.francesinhas.events.EventBusHook;
import pt.castro.francesinhas.events.ListRefreshEvent;
import pt.castro.francesinhas.events.ListRetrievedEvent;
import pt.castro.francesinhas.events.PlaceAlreadyExistsEvent;
import pt.castro.francesinhas.events.PlacePickerEvent;
import pt.castro.francesinhas.events.ScoreChangeEvent;
import pt.castro.francesinhas.events.UserClickEvent;
import pt.castro.francesinhas.events.UserDataEvent;
import pt.castro.francesinhas.tools.NotificationTools;
import pt.castro.francesinhas.tools.PlaceUtils;

public class ListActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private UserHolder mCurrentUser;
    private ListFragment mListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        mListFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        new EndpointGetItems().execute();
        getUserData();
    }

    private void getUserData() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            new UserEndpointActions(UserEndpointActions.GET_USER).execute(AccessToken.getCurrentAccessToken().getUserId());
        } else {
            mListFragment.setVoting(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_logout).setTitle(AccessToken.getCurrentAccessToken() != null ?
                R.string.action_logout : R.string.action_login);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logOut();
            startLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void logOut() {
        LoginManager.getInstance().logOut();
        mListFragment.setVoting(false);
    }

    @EventBusHook
    public void onEvent(final UserDataEvent userDataEvent) {
        if (userDataEvent.getUserHolder() == null) {
            new UserEndpointActions(UserEndpointActions.ADD_USER).execute(AccessToken.getCurrentAccessToken().getUserId());
        } else {
            mCurrentUser = userDataEvent.getUserHolder();
            mListFragment.setVoting(true);
            new EndpointGetItems().execute();
        }
    }

    @EventBusHook
    public void onEvent(final ListRetrievedEvent listRetrievedEvent) {
        final List<LocalItemHolder> localItemHolders = new ArrayList<>();

        // Creates a blank map in case the user has no votes.
        JsonMap map = new JsonMap();
        if (mCurrentUser != null) {
            map = mCurrentUser.getVotes();
        }

        // Iterates all retrieved items and adds votes when applied.
        for (ItemHolder itemHolder : listRetrievedEvent.list) {
            LocalItemHolder localItemHolder = new LocalItemHolder(itemHolder);
            BigDecimal vote = (BigDecimal) map.get(itemHolder.getId());
            int voteInt = vote != null ? vote.intValueExact() : 0;
            localItemHolder.setUserVote(voteInt);
            localItemHolders.add(localItemHolder);
        }

        // Passes the generated list to the adapter.
        mListFragment.setItems(localItemHolders);
    }

    @EventBusHook
    public void onEvent(final UserClickEvent userClickEvent) {
        if (mCurrentUser != null && userClickEvent.getItemHolder() != null) {
            new EndpointUserVote(mCurrentUser.getId(), userClickEvent.getItemHolder().getId())
                    .execute(userClickEvent.getUserVote());
        } else {
            NotificationTools.toastLoggedVote(this);
        }
    }

    @EventBusHook
    public void onEvent(final ScoreChangeEvent scoreChangeEvent) {
        mCurrentUser = scoreChangeEvent.getUserHolder();
    }

    @EventBusHook
    public void onEvent(final ListRefreshEvent listRefreshEvent) {
        if (!listRefreshEvent.isRefreshed()) {
            new EndpointGetItems().execute();
        }
    }

    @EventBusHook
    public void onEvent(final PlacePickerEvent placePickerEvent) {
        if (mCurrentUser == null) {
            NotificationTools.toastLoggedAdd(this);
            return;
        }
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                e) {
            NotificationTools.toastGoogleConnectionFailed(this);
            // TODO
            e.printStackTrace();
        }
    }

    @EventBusHook
    public void onEventMainThread(final PlaceAlreadyExistsEvent placeAlreadyExistsEvent) {
        NotificationTools.toastCustomText(this, R.string.place_exists);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ActivityResult", "Request code: " + requestCode + "\tResult code: " + resultCode);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(data, this);
            if (!place.getPlaceTypes().contains(Place.TYPE_RESTAURANT) && !place.getPlaceTypes()
                    .contains(Place.TYPE_CAFE) && !place.getPlaceTypes().contains(Place
                    .TYPE_FOOD)) {
                NotificationTools.toastCustomText(this, R.string.invalid_place);
            } else {
                new EndpointsAsyncTask(EndpointsAsyncTask.ADD).execute(PlaceUtils.getItemFromPlace(this, place));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (AccessToken.getCurrentAccessToken() == null) {
            startLoginActivity();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}