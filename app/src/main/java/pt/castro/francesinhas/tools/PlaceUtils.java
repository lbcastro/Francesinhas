package pt.castro.francesinhas.tools;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.places.Place;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.castro.francesinhas.backend.myApi.model.ItemHolder;

/**
 * Created by lourenco.castro on 02-06-2015.
 */
public class PlaceUtils {

    public static ItemHolder getItemFromPlace(final Context context, final Place place) {
        ItemHolder itemHolder = new ItemHolder();
        itemHolder.setName(place.getName().toString());
        itemHolder.setId(place.getId());
        itemHolder.setAddress(place.getAddress().toString());
        itemHolder.setPhone(place.getPhoneNumber().toString());
        itemHolder.setPriceRange(place.getPriceLevel());
        itemHolder.setLocation(getCityName(context, place));
        itemHolder.setLatitude(place.getLatLng().latitude);
        itemHolder.setLongitude(place.getLatLng().longitude);
        if (place.getWebsiteUri() != null) {
            itemHolder.setUrl(place.getWebsiteUri().toString());
        } else {
            itemHolder.setUrl("n/a");
        }
        itemHolder.setPhotoUrl("n/a");
        return itemHolder;
    }

    private static String getCityName(Context context, Place place) {
        Location location = new Location(place.getName().toString());
        location.setLatitude(place.getLatLng().latitude);
        location.setLongitude(place.getLatLng().longitude);
        location.setTime(new Date().getTime());
        StringBuilder address = new StringBuilder();
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location
                    .getLongitude(), 1);
            if (addresses.size() > 0) address.append(addresses.get(0).getAddressLine(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: Figure out if this works consistently
        return address.toString().replaceAll("\\d", "").replaceAll("^-+", "").trim();
    }

    public static String placeToString(final ItemHolder itemHolder) {
        return itemHolder.getName() + "\t" + itemHolder.getLocation() + "\t" +
                itemHolder.getAddress() + "\t" + itemHolder.getPhone() + "\t" +
                itemHolder.getLatitude() + "\t" + itemHolder.getLongitude();
    }
}
