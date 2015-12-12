package pt.castro.francesinhas.list;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import icepick.Icepick;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.backend.myApi.model.JsonMap;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.francesinhas.communication.EndpointGetItems;
import pt.castro.francesinhas.communication.EndpointUserVote;
import pt.castro.francesinhas.communication.EndpointsAsyncTask;
import pt.castro.francesinhas.communication.GetPlacePhotos;
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
import pt.castro.francesinhas.list.decoration.TransitionUtils;
import pt.castro.francesinhas.tools.LayoutUtils;
import pt.castro.francesinhas.tools.NotificationTools;
import pt.castro.francesinhas.tools.PlaceUtils;

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
    //    private ListFragment mListFragment;
    private SearchView mSearchView;
    private CustomRecyclerViewAdapter recyclerViewAdapter;

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
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.fragment_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(TransitionUtils.makeFadeTransition());
            getWindow().setExitTransition(TransitionUtils.makeFadeTransition());
        }

        ButterKnife.bind(this);
        LayoutUtils.initImageLoader(getApplicationContext());
        setRecyclerView();

        getUserData();
    }

    private void setRecyclerView() {
        recyclerViewAdapter = new CustomRecyclerViewAdapter();
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout
                .OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventBus.getDefault().post(new ListRefreshEvent(false));
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

    public void setItems(final List<LocalItemHolder> items) {
        recyclerViewAdapter.setItems(items);
        if (mainRecyclerView.getAdapter() == null) {
            mainRecyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    public void setEmptyList(final String message) {
        setItems(new ArrayList<LocalItemHolder>());
        final TextView emptyText = (TextView) mainRecyclerView.findViewById(R.id
                .empty_text);
        emptyText.setText(message);
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
        floatingActionButton.show();
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
            new EndpointGetItems().execute();
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
    public void onEvent(final ConnectionFailedEvent connectionFailedEvent) {
        setEmptyList("No connection");
        setButton(false);
    }

    @EventBusHook
    public void onEventMainThread(final ListRetrievedEvent listRetrievedEvent) {
        final List<LocalItemHolder> localItemHolders = new ArrayList<>();

        // Iterates all retrieved items and adds votes when applied.
        for (ItemHolder itemHolder : listRetrievedEvent.list) {
            LocalItemHolder localItemHolder = new LocalItemHolder(itemHolder);
            int voteInt = getVote(mCurrentUser, itemHolder.getId());
            localItemHolder.setUserVote(voteInt);
//            if (itemHolder.getPhotoUrl() == null || itemHolder.getPhotoUrl().equals
// ("n/a")) {
//                final GetPlacePhotos getPlacePhotos = new GetPlacePhotos
// (localItemHolder);
//                getPlacePhotos.getAllPhotos();
//            }
            localItemHolders.add(localItemHolder);
            if (itemHolder.getLatitude() == null || itemHolder.getLongitude() == null) {
                GetPlacePhotos getPlacePhotos = new GetPlacePhotos(localItemHolder);
                getPlacePhotos.getLocation();
            }
        }

        // Passes the generated list to the adapter.
        setItems(localItemHolders);
    }

    @EventBusHook
    public void onEvent(final UserClickEvent userClickEvent) {
        if (userClickEvent.getLocalItemHolder() == null || userClickEvent
                .getLocalItemHolder().getItemHolder() == null) {
            NotificationTools.toastLoggedVote(this);
            return;
        }
        if (userClickEvent.getUserVote() != 0 && mCurrentUser != null) {
            final ItemHolder itemHolder = userClickEvent.getLocalItemHolder()
                    .getItemHolder();
            new EndpointUserVote(mCurrentUser.getId(), itemHolder.getId()).execute
                    (userClickEvent.getUserVote());
        } else {
            showDetailsFragment(userClickEvent.getLocalItemHolder(), userClickEvent
                    .getView());
        }
    }

    private void showDetailsFragment(final LocalItemHolder localItemHolder, View view) {
        floatingActionButton.hide();
        final ItemHolder itemHolder = localItemHolder.getItemHolder();
        final Intent intent = new Intent(this, DetailsActivity.class);
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
        intent.putExtra(DetailsKeys.USER_ID, mCurrentUser != null ? mCurrentUser.getId
                () : "");
        intent.putExtra(DetailsKeys.USER_VOTE, localItemHolder.getUserVote());


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            View image = view.findViewById(R.id.backdrop_image);
            View overlay = view.findViewById(R.id.backdrop_clickable);
            View statusBar = findViewById(android.R.id.statusBarBackground);
            View navigationBar = findViewById(android.R.id.navigationBarBackground);

            List<Pair<View, String>> pairs = new ArrayList<>();
            pairs.add(Pair.create(statusBar, Window
                    .STATUS_BAR_BACKGROUND_TRANSITION_NAME));
            pairs.add(Pair.create(navigationBar, Window
                    .NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
            pairs.add(Pair.create(image, getString(R.string.transition_image)));
            pairs.add(Pair.create(overlay, getString(R.string.transition_clickable)));
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, pairs.toArray(new Pair[pairs
                            .size()]));
            ActivityCompat.startActivity(this, intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @EventBusHook
    public void onEvent(final ScoreChangeEvent scoreChangeEvent) {
        mCurrentUser = scoreChangeEvent.getUserHolder();
    }

    @EventBusHook
    public void onEvent(final ListRefreshEvent listRefreshEvent) {
        if (!listRefreshEvent.isRefreshed()) {
            // TODO: Instead of retrieving a new set of items, update the existing ones
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
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException |
                GooglePlayServicesNotAvailableException e) {
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
        Log.d("ActivityResult", "Request code: " + requestCode + "\tResult code: " +
                resultCode);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(data, this);
            if (!place.getPlaceTypes().contains(Place.TYPE_RESTAURANT) && !place
                    .getPlaceTypes().contains(Place.TYPE_CAFE) && !place.getPlaceTypes
                    ().contains(Place.TYPE_FOOD)) {
                NotificationTools.toastCustomText(this, R.string.invalid_place);
            } else {
                final ItemHolder itemHolder = PlaceUtils.getItemFromPlace(this, place);
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