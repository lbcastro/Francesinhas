package pt.castro.tops;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import de.greenrobot.event.EventBus;
import pt.castro.tops.events.list.ListRefreshEvent;

/**
 * Created by lourenco on 28/02/16.
 */
public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient
        .OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean hasPermission;

    public void setup(final Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {
            hasPermission = false;
            return;
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }
        hasPermission = true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            final boolean hasLocation = CustomApplication.getPlacesManager().hasLocation();
            CustomApplication.getPlacesManager().setLocation(location);
            if (!hasLocation) {
                EventBus.getDefault().post(new ListRefreshEvent());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void start() {
        if (hasPermission) {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                CustomApplication.getPlacesManager().setLocation(location);
            }
        }
    }

    public void stop() {
        if (hasPermission && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
