package pt.castro.tops.list;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

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
import pt.castro.tops.communication.EndpointSearch;
import pt.castro.tops.communication.EndpointUserVote;
import pt.castro.tops.communication.EndpointsAsyncTask;
import pt.castro.tops.communication.IConnectionObserver;
import pt.castro.tops.communication.NetworkChangeMonitor;
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
import pt.castro.tops.tools.LayoutUtils;
import pt.castro.tops.tools.NotificationUtils;
import pt.castro.tops.tools.PlaceUtils;

public class ListActivity extends AppCompatActivity implements IConnectionObserver, SearchHelper
        .ISearchObserver {

    private static final int PLACE_PICKER_REQUEST = 1;
    private final String TAG = getClass().getName();

    @Bind(R.id.fragment_recycler_view)
    UltimateRecyclerView mainRecyclerView;
    @Bind(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;

//    private BottomBar mBottomBar;

    private RecyclerViewManager mRecyclerViewManager;
    private PermissionsManager mPermissionsManager;
    private LocationManager mLocationManager;

    private UserHolder mCurrentUser;
    private String mNextToken;

    private SearchHelper mSearchHelper;
    private NetworkChangeMonitor mConnectionMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        startLocationLayer();

        if (LayoutUtils.isKitkatOrAbove()) {
            LayoutUtils.setImmersiveMode(this);
            setFabMargin();
        }

        setToolbar();
//        setBottomBar(savedInstanceState);

        mRecyclerViewManager = new RecyclerViewManager();
        mRecyclerViewManager.setRecyclerView(this, mainRecyclerView);

        mConnectionMonitor = new NetworkChangeMonitor(this);
        if (mConnectionMonitor.checkConnection(this)) {
            getUserData();
        } else {
            notConnectedState();
        }
    }

    private void setFabMargin() {
        final int fabMargin = LayoutUtils.hasSoftKeys(this) ? getResources()
                .getDimensionPixelSize(R.dimen.margin_fab_soft_keys) : getResources()
                .getDimensionPixelOffset(R.dimen.margin_m);
        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                floatingActionButton.getLayoutParams();
        params.bottomMargin = fabMargin;
        floatingActionButton.setLayoutParams(params);
    }

//    private void setBottomBar(final Bundle savedInstanceState) {
////        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.coordinator),
////                findViewById(R.id.fragment_recycler_view), savedInstanceState);
//        mBottomBar = BottomBar.attachShy(coordinatorLayout, mainRecyclerView, savedInstanceState);
//        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
//            @Override
//            public void onMenuTabSelected(@IdRes int menuItemId) {
//                if (menuItemId == R.id.bottomBarItemTwo) {
//                    // The user selected item number one.
//                }
//            }
//
//            @Override
//            public void onMenuTabReSelected(@IdRes int menuItemId) {
//                if (menuItemId == R.id.bottomBarItemThree) {
//                    // The user reselected item number one, scroll your content to top.
//                }
//            }
//        });
//
//        // Setting colors for different tabs when there's more than three of them.
//        // You can set colors for tabs in three different ways as shown below.
////        mBottomBar.getBar().getLayoutParams().height = (int) getResources().getDimension(R
// .dimen.row_bottom_bar_height);
//        mBottomBar.setActiveTabColor(ContextCompat.getColor(this, R.color.blue));
////        mBottomBar.useDarkTheme();
//        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.dark_gray));
//        mBottomBar.mapColorForTab(1, 0xFF5D4037);
////        mBottomBar.mapColorForTab(2, "#7B1FA2");
////        mBottomBar.mapColorForTab(3, "#7B1FA2");
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (mPermissionsManager.hasLocationPermission(this)) {
                mLocationManager.setup(this);
                mLocationManager.start();
            } else {
                getItems();
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
        AppEventsLogger.activateApp(getApplication());
        if (mConnectionMonitor.isConnected()) {
            floatingActionButton.show();
//            mBottomBar.show();
        }
        mConnectionMonitor.registerConnectionMonitor(this);
        mRecyclerViewManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mConnectionMonitor.unregisterConnectionMonitor(this);
    }

    @Override
    public void onStop() {
        mLocationManager.stop();
        super.onStop();
        mConnectionMonitor.unregisterConnectionMonitor(this);
        mRecyclerViewManager.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mConnectionMonitor.unregisterConnectionMonitor(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_logout).setTitle(mCurrentUser != null ? R.string.action_logout
                : R.string.action_login);
        mSearchHelper = new SearchHelper(this);
        mSearchHelper.setSearchView(this, (SearchView) menu.findItem(R.id
                .options_menu_main_search).getActionView());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logOut();
        } else if (item.getItemId() == R.id.action_refresh) {
            if (mConnectionMonitor.isConnected()) {
                EventBus.getDefault().post(new ERequestRefresh());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mSearchHelper.isIconified()) {
            mSearchHelper.resetSearchView();
        } else if (mCurrentUser == null) {
            startLoginActivity();
            CustomApplication.getPlacesManager().clear();
            mConnectionMonitor.unregisterConnectionMonitor(this);
        } else {
            super.onBackPressed();
            CustomApplication.getPlacesManager().clear();
            mConnectionMonitor.unregisterConnectionMonitor(this);
        }
    }

    @OnClick(R.id.floating_action_button)
    public void onClickFloatingActionButton() {
        EventBus.getDefault().post(new PlacePickerEvent());
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

    @EventBusHook
    public void onEventMainThread(final ConnectionFailedEvent connectionFailedEvent) {
        notConnectedState();
    }

    @EventBusHook
    public void onEventMainThread(final ListRetrievedEvent listRetrievedEvent) {
        if (mNextToken == null) {
            mRecyclerViewManager.clear();
        }
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
        mRecyclerViewManager.setRefreshing(false);
    }

    @EventBusHook
    public void onEventMainThread(final EndpointSearch.ENoPlacesFound event) {
        if (CustomApplication.getPlacesManager().getPlaces().size() > 0) {
            return;
        }
        mNextToken = null;
        mRecyclerViewManager.setEmptyList(getString(R.string.no_places));
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        mRecyclerViewManager.setRefreshing(false);
        LayoutUtils.setToolbarCollapsible(toolbar, false);
    }

    @EventBusHook
    public void onEventMainThread(final ListRefreshEvent listRefreshEvent) {
        mSearchHelper.resetSearchView();
        refreshList();
    }

    @EventBusHook
    public void onEventMainThread(final PlaceAlreadyExistsEvent placeAlreadyExistsEvent) {
        NotificationUtils.toastCustomText(this, R.string.place_exists);
    }

    @EventBusHook
    public void onEventMainThread(final RecyclerViewManager.ELoadMore eLoadMore) {
        if (!mSearchHelper.isIconified()) {
            EndpointSearch endpointSearch = new EndpointSearch();
            endpointSearch.setCursor(mNextToken);
            endpointSearch.execute(mSearchHelper.getQuery());
        } else {
            getItems();
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
    public void onEvent(final ERequestRefresh eRequestRefresh) {
        if (!mConnectionMonitor.isConnected()) {
            if (!mConnectionMonitor.checkConnection(this)) {
                notConnectedState();
            } else {
                connectedState();
                getUserData();
            }
        } else {
            mSearchHelper.resetSearchView();
            refreshList();
        }
    }

    private void notConnectedState() {
        floatingActionButton.hide();
        mRecyclerViewManager.setConnected(false);
        mNextToken = null;
    }

    private void connectedState() {
        mRecyclerViewManager.setConnected(true);
        floatingActionButton.show();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar.setPadding(0, LayoutUtils.getStatusBarHeight(getResources()), 0, 0);
            toolbar.getLayoutParams().height = toolbar.getLayoutParams().height + LayoutUtils
                    .getStatusBarHeight(getResources());
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
    }

    private void refreshList() {
        // TODO: Instead of retrieving a new set of items, update the existing ones
        mNextToken = null;
        LayoutUtils.setToolbarCollapsible(toolbar, true);
        CustomApplication.getPlacesManager().clear();
        mRecyclerViewManager.setRefreshing(true);
        getItems();
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
        CustomApplication.getPlacesManager().clear();
        if (mCurrentUser == null) {
            mRecyclerViewManager.setVoting(false);
        } else {
            mRecyclerViewManager.setVoting(true);
            setButton(true);
        }
        getItems();
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

    @Override
    public void onSearchStart() {
        mRecyclerViewManager.setRefreshing(true);
    }

    @Override
    public void onSearchComplete() {
        mNextToken = null;
    }

    @Override
    public void onSearchReset() {
        refreshList();
    }

    @Override
    public void onConnect() {
        connectedState();
        getUserData();
    }

    @Override
    public void onDisconnect() {
        notConnectedState();
    }
}