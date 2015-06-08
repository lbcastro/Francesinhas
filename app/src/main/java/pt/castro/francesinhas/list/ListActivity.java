package pt.castro.francesinhas.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.francesinhas.communication.EndpointGetItems;
import pt.castro.francesinhas.communication.EndpointGetUser;
import pt.castro.francesinhas.communication.EndpointsAsyncTask;
import pt.castro.francesinhas.communication.login.EndpointUserVote;
import pt.castro.francesinhas.communication.login.LoginActivity;
import pt.castro.francesinhas.events.EventBusHook;
import pt.castro.francesinhas.events.ListRefreshEvent;
import pt.castro.francesinhas.events.ListRetrievedEvent;
import pt.castro.francesinhas.events.PlaceAlreadyExistsEvent;
import pt.castro.francesinhas.events.PlacePickerEvent;
import pt.castro.francesinhas.events.UserClickEvent;
import pt.castro.francesinhas.events.UserDataEvent;
import pt.castro.francesinhas.tools.LayoutUtils;
import pt.castro.francesinhas.tools.PlaceUtils;

public class ListActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private UserHolder mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        new EndpointGetItems().execute();
        getUserData();
    }

    private void getUserData() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            UserHolder userHolder = new UserHolder();
            userHolder.setId(accessToken.getUserId());
            userHolder.setToken(accessToken.getToken());
            new EndpointGetUser().execute(userHolder);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @EventBusHook
    public void onEvent(final UserDataEvent userDataEvent) {
        mCurrentUser = userDataEvent.getUserHolder();
    }

    @EventBusHook
    public void onEvent(final ListRetrievedEvent listRetrievedEvent) {
        final ListFragment fragment = (ListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        List<LocalItemHolder> localItemHolders = new ArrayList<>();
        for (ItemHolder itemHolder : listRetrievedEvent.list) {
            itemHolder.setBackgroundColor(getResources().getColor(R.color.row_color));
            LocalItemHolder localItemHolder = new LocalItemHolder(itemHolder);
//            int vote = (int) mCurrentUser.getVotes().get(itemHolder.getId());
//            if (vote != 0) {
//                localItemHolder.setUserVote(vote);
//            }
            localItemHolders.add(localItemHolder);
        }
        fragment.setItems(localItemHolders);
    }

    @EventBusHook
    public void onEvent(final UserClickEvent userClickEvent) {
        if (mCurrentUser != null) {
            new EndpointUserVote(mCurrentUser, userClickEvent.getItemHolder().getId())
                    .execute(userClickEvent.getUserVote());
        } else {
            Toast.makeText(this, "You need to be logged in to vote", Toast.LENGTH_SHORT).show();
        }
    }

    @EventBusHook
    public void onEvent(final ListRefreshEvent listRefreshEvent) {
        if (!listRefreshEvent.isRefreshed()) {
            new EndpointGetItems().execute();
        }
    }

    @EventBusHook
    public void onEvent(final PlacePickerEvent placePickerEvent) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                e) {
            // TODO
            e.printStackTrace();
        }
    }

    @EventBusHook
    public void onEventMainThread(final PlaceAlreadyExistsEvent placeAlreadyExistsEvent) {
        Toast.makeText(this, R.string.place_exists, Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ActivityResult", "Request code: " + requestCode + "\tResult code: " + resultCode);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(data, this);
            if (!place.getPlaceTypes().contains(Place.TYPE_RESTAURANT) && !place.getPlaceTypes()
                    .contains(Place.TYPE_CAFE) && !place.getPlaceTypes().contains(Place
                    .TYPE_FOOD)) {
                Toast.makeText(this, R.string.invalid_place, Toast.LENGTH_SHORT).show();
            } else {
                final EndpointsAsyncTask task = new EndpointsAsyncTask(EndpointsAsyncTask.ADD);
                final ItemHolder itemHolder = PlaceUtils.getItemFromPlace(this, place);
                itemHolder.setBackgroundColor(LayoutUtils.getRandomColor(this));
                task.execute(itemHolder);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}