package pt.castro.tops.list;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.tops.CustomApplication;
import pt.castro.tops.LocationManager;
import pt.castro.tops.PermissionsManager;
import pt.castro.tops.R;
import pt.castro.tops.communication.EndpointGetItems;
import pt.castro.tops.communication.EndpointUserVote;
import pt.castro.tops.communication.EndpointsAsyncTask;
import pt.castro.tops.communication.IConnectionObserver;
import pt.castro.tops.communication.NetworkChangeReceiver;
import pt.castro.tops.communication.login.LoginActivity;
import pt.castro.tops.details.DetailsActivity;
import pt.castro.tops.events.EventBusHook;
import pt.castro.tops.events.connection.ConnectionFailedEvent;
import pt.castro.tops.events.list.ListRefreshEvent;
import pt.castro.tops.events.list.ListRetrievedEvent;
import pt.castro.tops.events.place.PlaceAlreadyExistsEvent;
import pt.castro.tops.events.place.PlacePickerEvent;
import pt.castro.tops.events.place.ScoreChangeEvent;
import pt.castro.tops.events.user.UserClickEvent;
import pt.castro.tops.tools.ConnectionUtils;
import pt.castro.tops.tools.NotificationUtils;
import pt.castro.tops.tools.PlaceUtils;

public class ListActivity extends AppCompatActivity implements IConnectionObserver {

    private static final int PLACE_PICKER_REQUEST = 1;
    private final String TAG = getClass().getName();

    @Bind(R.id.fragment_recycler_view)
    UltimateRecyclerView mainRecyclerView;
    @Bind(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private BottomBar mBottomBar;

    private RecyclerViewManager mRecyclerViewManager;
    private PermissionsManager mPermissionsManager;
    private LocationManager mLocationManager;

//    private String mUserId;

    private UserHolder mCurrentUser;
    private SearchView mSearchView;
    private String mNextToken;

    private boolean mConnectedState;
    private NetworkChangeReceiver mConnectionMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        startLocationLayer();

        ButterKnife.bind(this);
        setToolbar();
//        setBottomBar(savedInstanceState);

        mRecyclerViewManager = new RecyclerViewManager();
        mRecyclerViewManager.setRecyclerView(this, mainRecyclerView);

        if (checkConnection()) {
            getUserData();
        } else {
            notConnectedState();
        }
    }

    private void setBottomBar(final Bundle savedInstanceState) {
        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.coordinator),
                findViewById(R.id.fragment_recycler_view), savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    // The user selected item number one.
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });

        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        mBottomBar.setActiveTabColor(ContextCompat.getColor(this, R.color.blue_bright));
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.blue_bright));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.blue_bright));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.blue_bright));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (mPermissionsManager.hasLocationPermission(this)) {
                mLocationManager.setup(this);
                mLocationManager.start();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mLocationManager.start();
        AppEventsLogger.activateApp(this);
        if (mConnectedState) {
            floatingActionButton.show();
//            mBottomBar.show();
        }
        registerConnectionMonitor();
        mRecyclerViewManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
        unregisterConnectionMonitor();
    }

    @Override
    public void onStop() {
        mLocationManager.stop();
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        unregisterConnectionMonitor();
        mRecyclerViewManager.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterConnectionMonitor();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_logout).setTitle(mCurrentUser != null ? R.string.action_logout
                : R.string.action_login);
        setSearchView((SearchView) menu.findItem(R.id.options_menu_main_search).getActionView());
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setQuery("", true);
            mSearchView.setIconified(true);
            mSearchView.clearFocus();
        } else if (mCurrentUser == null) {
            startLoginActivity();
            CustomApplication.getPlacesManager().clear();
            unregisterConnectionMonitor();
        } else {
            super.onBackPressed();
            CustomApplication.getPlacesManager().clear();
            unregisterConnectionMonitor();
        }
    }

    @Override
    public void onConnectionChange(boolean connected) {
        if (!mConnectedState && connected) {
            connectedState();
            getUserData();
        }
        if (!connected) {
            notConnectedState();
        }
        mConnectedState = connected;
    }

    @OnClick(R.id.floating_action_button)
    void onClickFloatingActionButton() {
        EventBus.getDefault().post(new PlacePickerEvent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logOut();
        } else if (item.getItemId() == R.id.action_refresh) {
            EventBus.getDefault().post(new ListRefreshEvent());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            if (!place.getPlaceTypes().contains(Place.TYPE_RESTAURANT) && !place.getPlaceTypes()
                    .contains(Place.TYPE_CAFE) && !place.getPlaceTypes().contains(Place
                    .TYPE_FOOD)) {
                NotificationUtils.toastCustomText(this, R.string.invalid_place);
            } else {
                final ItemHolder itemHolder = PlaceUtils.getItemFromPlace(this, place);
                itemHolder.setUserId(mCurrentUser.getId());
                new EndpointsAsyncTask(EndpointsAsyncTask.ADD).execute(itemHolder);
            }
        }
    }

//    @EventBusHook
//    public void onEvent(final UserDataEvent userDataEvent) {
//        mCurrentUser = userDataEvent.getUserHolder();
//        mRecyclerViewManager.setVoting(true);
//        setButton(true);
//        getItems();
//    }
//
//    @EventBusHook
//    public void onEvent(final NoUserEvent noUserEvent) {
//        new UserEndpointActions(UserEndpointActions.ADD_USER).execute(mUserId);
//    }

    @EventBusHook
    public void onEventMainThread(final ConnectionFailedEvent connectionFailedEvent) {
        notConnectedState();
    }

    @EventBusHook
    public void onEventMainThread(final ListRetrievedEvent listRetrievedEvent) {
        mNextToken = listRetrievedEvent.getToken();
        // Iterates all retrieved items and adds votes when applied.
        for (ItemHolder itemHolder : listRetrievedEvent.getList()) {
            LocalItemHolder localItemHolder = PlaceUtils.processItem(itemHolder, mCurrentUser);
            try {
                CustomApplication.getPlacesManager().add(itemHolder.getId(), localItemHolder);
                mRecyclerViewManager.add(localItemHolder);
            } catch (Exception ignored) {
                // Item already exists
            }
        }
    }

    @EventBusHook
    public void onEvent(final UserClickEvent userClickEvent) {
        if (userClickEvent.getLocalItemHolder() == null || userClickEvent.getLocalItemHolder()
                .getItemHolder() == null) {
            NotificationUtils.toastLoggedVote(this);
            return;
        }
        if (userClickEvent.getUserVote() != 0 && mCurrentUser != null) {
            final ItemHolder itemHolder = userClickEvent.getLocalItemHolder().getItemHolder();
            new EndpointUserVote(mCurrentUser.getId(), itemHolder.getId()).execute(userClickEvent
                    .getUserVote());
        } else {
            showDetails(userClickEvent.getLocalItemHolder());
        }
    }

    @EventBusHook
    public void onEvent(final ScoreChangeEvent scoreChangeEvent) {
        mCurrentUser = scoreChangeEvent.getUserHolder();
    }

    @EventBusHook
    public void onEventMainThread(final ListRefreshEvent listRefreshEvent) {
        // TODO: Instead of retrieving a new set of items, update the existing ones
        CustomApplication.getPlacesManager().clear();
        mRecyclerViewManager.setEmptyList(getString(R.string.loading));
        mNextToken = null;
        getItems();
    }

    @EventBusHook
    public void onEvent(final PlacePickerEvent placePickerEvent) {
        if (mCurrentUser == null) {
            NotificationUtils.toastLoggedAdd(this);
            return;
        }

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException |
                GooglePlayServicesNotAvailableException e) {
            NotificationUtils.toastGoogleConnectionFailed(this);
            // TODO
            e.printStackTrace();
        }
    }

    @EventBusHook
    public void onEventMainThread(final PlaceAlreadyExistsEvent placeAlreadyExistsEvent) {
        NotificationUtils.toastCustomText(this, R.string.place_exists);
    }

    @EventBusHook
    public void onEvent(final RecyclerViewManager.ERequestRefresh eRequestRefresh) {
        if (!mConnectedState) {
            if (!checkConnection()) {
                notConnectedState();
            } else {
                connectedState();
                getUserData();
            }
        } else {
            EventBus.getDefault().post(new ListRefreshEvent());
        }
    }

    @EventBusHook
    public void onEventMainThread(final RecyclerViewManager.ELoadMore eLoadMore) {
        getItems();
    }

    private void registerConnectionMonitor() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mConnectionMonitor = new NetworkChangeReceiver(this);
        registerReceiver(mConnectionMonitor, filter);
    }

    private void unregisterConnectionMonitor() {
        if (mConnectionMonitor != null) {
            try {
                unregisterReceiver(mConnectionMonitor);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void notConnectedState() {
        floatingActionButton.hide();
        mRecyclerViewManager.setNotConnected();
    }

    private void connectedState() {
        mainRecyclerView.hideEmptyView();
        floatingActionButton.show();
    }

    private boolean checkConnection() {
        mConnectedState = ConnectionUtils.checkConnection(this);
        return mConnectedState;
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
    }

    private void getItems() {
        if (mNextToken != null) {
            EndpointGetItems getItems = new EndpointGetItems();
            getItems.setCursor(mNextToken);
            getItems.execute();
        } else {
            new EndpointGetItems().execute();
        }
    }

    public void setButton(final boolean enabled) {
        floatingActionButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void getUserData() {
        mCurrentUser = CustomApplication.getUsersManager().getUser();
//        if (mUserId != null) {
//            CustomApplication.getPlacesManager().clear();
//            new UserEndpointActions(UserEndpointActions.GET_USER).execute(mUserId);
//        }
        if (mCurrentUser == null) {
            CustomApplication.getPlacesManager().clear();
            mRecyclerViewManager.setVoting(false);
            getItems();
        } else {
            CustomApplication.getPlacesManager().clear();
            mRecyclerViewManager.setVoting(true);
            setButton(true);
            getItems();
        }
    }

    private void setSearchView(final SearchView searchView) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = searchView;
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mRecyclerViewManager.setFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mRecyclerViewManager.setFilter(newText);
                return false;
            }
        });
    }


    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void logOut() {
        CustomApplication.getUsersManager().setUser(null);
        mRecyclerViewManager.setVoting(false);
        Intent intent = new Intent(this, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("logout", true);
        intent.putExtras(bundle);
        finish();
        startActivity(intent);
    }

    private void showDetails(final LocalItemHolder localItemHolder) {
        floatingActionButton.hide();
//        mBottomBar.hide();
        final ItemHolder itemHolder = localItemHolder.getItemHolder();
        final Intent intent = new Intent(this, DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id", itemHolder.getId());
        startActivity(intent);
    }

    private void startLocationLayer() {
        mLocationManager = new LocationManager();
        mPermissionsManager = new PermissionsManager();
        mPermissionsManager.verifyPermissions(this);
        if (mPermissionsManager.hasLocationPermission(this)) {
            mLocationManager.setup(this);
            mLocationManager.start();
        }
    }
}