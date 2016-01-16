package pt.castro.francesinhas.list;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import icepick.Icepick;
import pt.castro.francesinhas.CustomApplication;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.backend.myApi.model.JsonMap;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.francesinhas.communication.EndpointGetItems;
import pt.castro.francesinhas.communication.EndpointUserVote;
import pt.castro.francesinhas.communication.EndpointsAsyncTask;
import pt.castro.francesinhas.communication.GetGoogleData;
import pt.castro.francesinhas.communication.GetZomatoData;
import pt.castro.francesinhas.communication.UserEndpointActions;
import pt.castro.francesinhas.communication.login.LoginActivity;
import pt.castro.francesinhas.details.DetailsActivity;
import pt.castro.francesinhas.details.DetailsKeys;
import pt.castro.francesinhas.events.EventBusHook;
import pt.castro.francesinhas.events.connection.ConnectionFailedEvent;
import pt.castro.francesinhas.events.list.ListRefreshEvent;
import pt.castro.francesinhas.events.list.ListRetrievedEvent;
import pt.castro.francesinhas.events.place.PlaceAlreadyExistsEvent;
import pt.castro.francesinhas.events.place.PlacePickerEvent;
import pt.castro.francesinhas.events.place.ScoreChangeEvent;
import pt.castro.francesinhas.events.user.NoUserEvent;
import pt.castro.francesinhas.events.user.UserClickEvent;
import pt.castro.francesinhas.events.user.UserDataEvent;
import pt.castro.francesinhas.list.decoration.CustomItemDecoration;
import pt.castro.francesinhas.tools.LayoutUtils;
import pt.castro.francesinhas.tools.NotificationUtils;
import pt.castro.francesinhas.tools.PlaceUtils;
import pt.castro.francesinhas.tools.TransitionUtils;

public class ListActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private final String TAG = getClass().getName();

    @Bind(R.id.fragment_recycler_view)
    UltimateRecyclerView mainRecyclerView;
    @Bind(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private UserHolder mCurrentUser;
    private SearchView mSearchView;
    private CustomRecyclerViewAdapter recyclerViewAdapter;
    private String mNextToken;

    private boolean mConnectedState;

    private static int getVote(UserHolder userHolder, String itemId) {
        if (userHolder == null) {
            return 0;
        }
        JsonMap map = userHolder.getVotes();
        if (map == null || map.get(itemId) == null) {
            return 0;
        }
        final BigDecimal vote = (BigDecimal) map.get(itemId);
        return vote != null ? vote.intValueExact() : 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(TransitionUtils.makeFadeTransition());
            getWindow().setExitTransition(TransitionUtils.makeFadeTransition());
        }
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.fragment_list);

        ButterKnife.bind(this);
        LayoutUtils.initImageLoader(getApplicationContext());
        setRecyclerView();

        if (checkConnection()) {
            getUserData();
        } else {
            notConnectedState();
        }
    }

    private void notConnectedState() {
        setEmptyList("You're not connected!");
        floatingActionButton.hide();
    }

    private void connectedState() {
        mainRecyclerView.hideEmptyView();
        floatingActionButton.show();
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        mConnectedState = activeNetwork != null && activeNetwork
                .isConnectedOrConnecting();
        return mConnectedState;
    }

    private void setRecyclerView() {
        recyclerViewAdapter = new CustomRecyclerViewAdapter(this);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout
                .OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mConnectedState) {
                    if (!checkConnection()) {
                        notConnectedState();
                    } else {
                        connectedState();
                        getUserData();
                    }
                } else {
                    EventBus.getDefault().post(new ListRefreshEvent(false));
                }
            }
        });
        mainRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    ImageLoader.getInstance().pause();
                } else {
                    ImageLoader.getInstance().resume();
                }
            }
        });
        mainRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView
                .OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                EndpointGetItems getItems = new EndpointGetItems();
                getItems.setCursor(mNextToken);
                getItems.execute();
            }
        });
        mainRecyclerView.setAdapter(recyclerViewAdapter);
        mainRecyclerView.enableLoadmore();
        setEmptyList("Loading...");
        mainRecyclerView.addItemDecoration(new CustomItemDecoration());
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @OnClick(R.id.floating_action_button)
    void onClickFloatingActionButton() {
        EventBus.getDefault().post(new PlacePickerEvent());
    }

    public void setEmptyList(final String message) {
//        CustomApplication.getPlacesManager().clear();
        recyclerViewAdapter.clear();
        final TextView emptyText = (TextView) mainRecyclerView.findViewById(R.id
                .empty_text);
        emptyText.setText(message);
        mainRecyclerView.showEmptyView();
    }

    public void setButton(final boolean enabled) {
        floatingActionButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void setFilter(final String filter) {
        recyclerViewAdapter.setFilter(filter);
    }

    public void setVoting(final boolean enabled) {
        recyclerViewAdapter.setVoting(enabled);
    }

    private void getUserData() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            new UserEndpointActions(UserEndpointActions.GET_USER).execute(AccessToken
                    .getCurrentAccessToken().getUserId());
        } else {
            setVoting(false);
            new EndpointGetItems().execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        AppEventsLogger.activateApp(this);
        if (mConnectedState) {
            floatingActionButton.show();
        }
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
        menu.findItem(R.id.action_logout).setTitle(AccessToken.getCurrentAccessToken()
                != null ? R.string.action_logout : R.string.action_login);
        SearchManager searchManager = (SearchManager) getSystemService(Context
                .SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.options_menu_main_search)
                .getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName
                ()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setFilter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logOut();
            startLoginActivity();
        } else if (item.getItemId() == R.id.action_refresh) {
            EventBus.getDefault().post(new ListRefreshEvent(false));
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
        setVoting(false);
    }

    @EventBusHook
    public void onEvent(final UserDataEvent userDataEvent) {
        mCurrentUser = userDataEvent.getUserHolder();
        setVoting(true);
        setButton(true);
        new EndpointGetItems().execute();
    }

    @EventBusHook
    public void onEvent(final NoUserEvent noUserEvent) {
        new UserEndpointActions(UserEndpointActions.ADD_USER).execute(AccessToken
                .getCurrentAccessToken().getUserId());
    }

    @EventBusHook
    public void onEventMainThread(final ConnectionFailedEvent connectionFailedEvent) {
        notConnectedState();
    }

    @EventBusHook
    public void onEventMainThread(final ListRetrievedEvent listRetrievedEvent) {
        mainRecyclerView.hideEmptyView();
        mNextToken = listRetrievedEvent.getToken();
        if (mainRecyclerView.getAdapter() == null) {
            mainRecyclerView.setAdapter(recyclerViewAdapter);
        }

        // Iterates all retrieved items and adds votes when applied.
        for (ItemHolder itemHolder : listRetrievedEvent.getList()) {
            LocalItemHolder localItemHolder = new LocalItemHolder(itemHolder);
            int voteInt = getVote(mCurrentUser, itemHolder.getId());
            localItemHolder.setUserVote(voteInt);
            if (itemHolder.getLatitude() == null || itemHolder.getLongitude() == null
                    || (itemHolder.getLatitude() == 0 && itemHolder.getLongitude() ==
                    0)) {
                GetGoogleData getGoogleData = new GetGoogleData(localItemHolder);
                getGoogleData.getLocation();
            }
            if (itemHolder.getGoogleUrl() == null) {
                GetGoogleData getGoogleData = new GetGoogleData(localItemHolder);
                getGoogleData.getRating();
            }
            if (itemHolder.getZomatoUrl() == null) {
                GetZomatoData getZomatoData = new GetZomatoData(localItemHolder);
                getZomatoData.getData(itemHolder.getName());
            }
            String address = itemHolder.getAddress();
            String[] data = address.split(",");
            if (data[data.length - 2].equals(data[data.length - 1])) {
                itemHolder.setLocation(data[data.length - 1].trim());
                address = address.substring(0, address.lastIndexOf(","));
                itemHolder.setAddress(address.trim());
                EndpointsAsyncTask task = new EndpointsAsyncTask(EndpointsAsyncTask
                        .UPDATE);
                task.execute(itemHolder);
            }
            CustomApplication.getPlacesManager().add(itemHolder.getId(), localItemHolder);
            new GetGoogleData(localItemHolder).getAllPhotos();
            recyclerViewAdapter.add(localItemHolder);
        }
    }

    @EventBusHook
    public void onEvent(final UserClickEvent userClickEvent) {
        if (userClickEvent.getLocalItemHolder() == null || userClickEvent
                .getLocalItemHolder().getItemHolder() == null) {
            NotificationUtils.toastLoggedVote(this);
            return;
        }
        if (userClickEvent.getUserVote() != 0 && mCurrentUser != null) {
            final ItemHolder itemHolder = userClickEvent.getLocalItemHolder()
                    .getItemHolder();
            new EndpointUserVote(mCurrentUser.getId(), itemHolder.getId()).execute
                    (userClickEvent.getUserVote());
        } else {
            showDetailsFragment(userClickEvent.getLocalItemHolder());
        }
    }

    private void showDetailsFragment(final LocalItemHolder localItemHolder) {
        floatingActionButton.hide();
        final ItemHolder itemHolder = localItemHolder.getItemHolder();
        final Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("id", itemHolder.getId());
        intent.putExtra(DetailsKeys.ITEM_NAME, itemHolder.getName());
        intent.putExtra(DetailsKeys.ITEM_ADDRESS, itemHolder.getAddress());
        intent.putExtra(DetailsKeys.ITEM_PHONE, itemHolder.getPhone());
        intent.putExtra(DetailsKeys.ITEM_URL, itemHolder.getUrl());
        intent.putExtra(DetailsKeys.ITEM_VOTES_UP, itemHolder.getVotesUp());
        intent.putExtra(DetailsKeys.ITEM_VOTES_DOWN, itemHolder.getVotesDown());
        intent.putExtra(DetailsKeys.ITEM_BACKGROUND_URL, itemHolder.getPhotoUrl());
        intent.putExtra(DetailsKeys.ITEM_LATITUDE, itemHolder.getLatitude());
        intent.putExtra(DetailsKeys.ITEM_LONGITUDE, itemHolder.getLongitude());
        intent.putExtra(DetailsKeys.ITEM_ID, itemHolder.getId());
        intent.putExtra(DetailsKeys.ITEM_PRICE, itemHolder.getPriceRange());
        intent.putExtra(DetailsKeys.USER_ID, mCurrentUser != null ? mCurrentUser.getId
                () : "");
        intent.putExtra(DetailsKeys.USER_VOTE, localItemHolder.getUserVote());
        startActivity(intent);
    }

    @EventBusHook
    public void onEvent(final ScoreChangeEvent scoreChangeEvent) {
        mCurrentUser = scoreChangeEvent.getUserHolder();
    }

    @EventBusHook
    public void onEvent(final ListRefreshEvent listRefreshEvent) {
        if (!listRefreshEvent.isRefreshed()) {
            // TODO: Instead of retrieving a new set of items, update the existing ones
            CustomApplication.getPlacesManager().clear();
            recyclerViewAdapter.clear();
            new EndpointGetItems().execute();
        }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ActivityResult", "Request code: " + requestCode + "\tResult code: " +
                resultCode);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            if (!place.getPlaceTypes().contains(Place.TYPE_RESTAURANT) && !place
                    .getPlaceTypes().contains(Place.TYPE_CAFE) && !place.getPlaceTypes
                    ().contains(Place.TYPE_FOOD)) {
                NotificationUtils.toastCustomText(this, R.string.invalid_place);
            } else {
                final ItemHolder itemHolder = PlaceUtils.getItemFromPlace(this, place);
                Log.d("List", PlaceUtils.placeToString(itemHolder));
                itemHolder.setUserId(mCurrentUser.getId());
                new EndpointsAsyncTask(EndpointsAsyncTask.ADD).execute(itemHolder);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setQuery("", true);
            mSearchView.setIconified(true);
            mSearchView.clearFocus();
        } else if (AccessToken.getCurrentAccessToken() == null) {
            startLoginActivity();
        } else {
            super.onBackPressed();
        }
    }
}