package pt.castro.tops.tools;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.places.Place;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.backend.myApi.model.JsonMap;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.tops.communication.EndpointsAsyncTask;
import pt.castro.tops.communication.GetGoogleData;
import pt.castro.tops.communication.GetZomatoData;
import pt.castro.tops.list.LocalItemHolder;

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

    public static LocalItemHolder processItem(final ItemHolder itemHolder, final UserHolder
            userHolder) {
        LocalItemHolder localItemHolder = new LocalItemHolder(itemHolder);
        final int voteInt = getVote(userHolder, itemHolder.getId());
        localItemHolder.setUserVote(voteInt);

        // Google data
        if (itemHolder.getLatitude() == null || itemHolder.getLongitude() == null ||
                (itemHolder.getLatitude() == 0 && itemHolder.getLongitude() == 0)) {
            GetGoogleData getGoogleData = new GetGoogleData(localItemHolder);
            getGoogleData.getLocation();
        }
        if (itemHolder.getGoogleUrl() == null) {
            GetGoogleData getGoogleData = new GetGoogleData(localItemHolder);
            getGoogleData.getRating();
        }

        // Zomato data
        if (itemHolder.getZomatoUrl() == null) {
            GetZomatoData getZomatoData = new GetZomatoData(localItemHolder);
            getZomatoData.getData(itemHolder.getName());
        }

        // Post-process
        String address = itemHolder.getAddress();
        final String[] data = address.split(",");
        if (data[data.length - 2].equals(data[data.length - 1])) {
            itemHolder.setLocation(data[data.length - 1].trim());
            address = address.substring(0, address.lastIndexOf(","));
            itemHolder.setAddress(address.trim());
            EndpointsAsyncTask task = new EndpointsAsyncTask(EndpointsAsyncTask.UPDATE);
            task.execute(itemHolder);
        }
        return localItemHolder;
    }
}
