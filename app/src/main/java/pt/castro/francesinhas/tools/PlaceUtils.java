package pt.castro.francesinhas.tools;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.places.Place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.communication.EndpointsAsyncTask;

/**
 * Created by lourenco.castro on 02-06-2015.
 */
public class PlaceUtils {

    public static ItemHolder getItemFromPlace(final Context context, final Place place) {
        return new ItemHolder().setName(place.getName().toString()).setId(place.getId())
                .setAddress(place.getAddress().toString()).setPhone(place.getPhoneNumber()
                        .toString()).setPriceRange(place.getPriceLevel()).setLocation(getCityName
                        (context, place)).setUrl(place.getWebsiteUri().toString())
                .setGoogleRating(place.getRating());
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
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0)
                address.append(addresses.get(0).getAddressLine(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: Figure out if this works consistently
        return address.toString().replaceAll("\\d", "").replaceAll("^-+", "").trim();
    }

    public static List<ItemHolder> generateDummyList(final Context context) {
        int[] images = {R.drawable.francesinha1, R.drawable.francesinha2, R.drawable
                .francesinha3, R.drawable.francesinha4, R.drawable.francesinha5, R.drawable
                .francesinha6, R.drawable.francesinha7, R.drawable.francesinha8, R.drawable
                .francesinha9};
        String[] names = {"Alicantina", "Cufra", "Cunha", "Santiago", "Capa Negra", "Paquete",
                "Galiza", "Porto Beer", "Rio de Janeiro"};
        String[] locations = {"Porto", "Vila do Conde", "Matosinhos", "Gaia", "Maia", "Povoa do " +
                "Varzim", "Baixa", "Ribeira", "Antas"};
        final List<ItemHolder> items = new ArrayList<>();
        for (int x = 0; x < names.length; x++) {
            ItemHolder itemHolder = new ItemHolder();
            itemHolder.setName(names[x]);
            itemHolder.setLocation(locations[x]);
            itemHolder.setId(names[x]);
            items.add(itemHolder);
            EndpointsAsyncTask task = new EndpointsAsyncTask(EndpointsAsyncTask.ADD);
            task.execute(itemHolder);
        }
        return items;
    }
}
