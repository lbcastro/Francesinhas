package pt.castro.francesinhas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.communication.EndpointGetItems;
import pt.castro.francesinhas.communication.EndpointsAsyncTask;
import pt.castro.francesinhas.events.ListRefreshEvent;
import pt.castro.francesinhas.events.ListRetrievedEvent;
import pt.castro.francesinhas.events.PlaceAlreadyExistsEvent;
import pt.castro.francesinhas.events.PlacePickerEvent;

public class MainActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        new EndpointGetItems().execute();
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
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @EventBusHook
    public void onEvent(final ListRetrievedEvent listRetrievedEvent) {
        final MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        for (ItemHolder itemHolder : listRetrievedEvent.list) {
            itemHolder.setBackgroundColor(getResources().getColor(R.color.row_color));
        }
        fragment.setItems(listRetrievedEvent.list);
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
}