package pt.castro.tops.communication;

import android.location.Location;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;

import de.greenrobot.event.EventBus;
import pt.castro.tops.events.place.PhotoUpdateEvent;
import pt.castro.tops.list.LocalItemHolder;

/**
 * Created by lourenco on 06/01/16.
 */
public class GetZomatoData extends AsyncTask<String, Void, String> {

    private final static String ZOMATO_API_KEY = "c8184d9b28b60480e12821bca231b253";

    private LocalItemHolder localItemHolder;
    private String placeName;
    private boolean reverse;

    public GetZomatoData(final LocalItemHolder localItemHolder) {
        this.localItemHolder = localItemHolder;
    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public void getData(String placeName) {
        this.placeName = Normalizer.normalize(placeName, Normalizer.Form.NFD);
        this.placeName = this.placeName.replaceAll("[^\\p{ASCII}]", "");

        final String latitude = Double.toString(localItemHolder.getItemHolder().getLatitude());
        final String longitude = Double.toString(localItemHolder.getItemHolder().getLongitude());
        final String url = "https://developers.zomato.com/api/v2.1/search?q=" +
                this.placeName.replaceAll(" ", "%20") +
                "&lat=" + latitude + "&lon=" + longitude;

        int index = reverse ? this.placeName.indexOf(" ") : this.placeName.lastIndexOf(" ");
        if (index > 0) {
            if (reverse) {
                this.placeName = this.placeName.substring(index, placeName.length());
            } else {
                this.placeName = this.placeName.substring(0, index);
            }
        } else {
            this.placeName = "";
        }

        if (placeName.isEmpty()) {
            if (!reverse) {
                reverse = true;
                this.placeName = localItemHolder.getItemHolder().getName();
                getData(this.placeName);
            }
            return;
        }

        this.execute(url);
    }

    protected String doInBackground(String... urls) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final URL url = new URL(urls[0]);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("user_key", ZOMATO_API_KEY);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(connection
                        .getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
        } catch (MalformedURLException e) {
            // TODO: Attempt to correct the URL here
            e.printStackTrace();
        } catch (IOException e) {
            return null;
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String string) {
        if (string == null || string.isEmpty()) {
            if (!placeName.trim().isEmpty()) {
                new GetZomatoData(localItemHolder).getData(placeName);
            } else {
                localItemHolder.getItemHolder().setPriceRange(-2);
                EndpointsAsyncTask endpointsAsyncTask = new EndpointsAsyncTask(EndpointsAsyncTask
                        .UPDATE);
                endpointsAsyncTask.execute(localItemHolder.getItemHolder());
            }
            return;
        }

        try {
            final JSONObject object = new JSONObject(string);
            process(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void process(final JSONObject jsonObject) {
        try {
            JSONArray restaurantsArray = jsonObject.getJSONArray("restaurants");
            boolean match = false;
            JSONObject restaurant = null;
            String name = "";

            final Location localLocation = new Location("");
            localLocation.setLatitude(localItemHolder.getItemHolder().getLatitude());
            localLocation.setLongitude(localItemHolder.getItemHolder().getLongitude());

            for (int x = 0; x < Math.min(restaurantsArray.length(), 5); x++) {
                restaurant = restaurantsArray.getJSONObject(x).getJSONObject("restaurant");
                JSONObject location = restaurant.getJSONObject("location");
                final Location temp = new Location("");
                temp.setLatitude(location.getDouble("latitude"));
                temp.setLongitude(location.getDouble("longitude"));
                if (localLocation.distanceTo(temp) < 200) {
                    match = true;
                    name = restaurant.getString("name");
                    break;
                }
            }

            if (!match && !localItemHolder.getItemHolder().getPhone().isEmpty()) {
                for (int x = 0; x < Math.min(restaurantsArray.length(), 5); x++) {
                    restaurant = restaurantsArray.getJSONObject(x).getJSONObject("restaurant");
                    try {
                        final String phone = restaurant.getString("phone_numbers");
                        if (similarity(localItemHolder.getItemHolder().getPhone(), phone) > 0.51) {
                            match = true;
                            break;
                        }
                    } catch (JSONException ignored) {
                        // No phone number on Zomato
                    }
                }
            }

            if (!match) {
                for (int x = 0; x < Math.min(restaurantsArray.length(), 5); x++) {
                    restaurant = restaurantsArray.getJSONObject(x).getJSONObject("restaurant");
                    name = restaurant.getString("name");
                    double similarity = similarity(name, this.placeName);
                    if (similarity > 0.51) {
                        match = true;
                        break;
                    }
                }
            }

            if (!match) {
                new GetZomatoData(localItemHolder).getData(placeName);
                return;
            }

            // TODO: Verify restaurant object

            int priceRange = restaurant.getInt("price_range");
            String photoUrl = restaurant.getString("featured_image");

            JSONObject location = restaurant.getJSONObject("location");
            String address = location.getString("address");
            String city = location.getString("city");
            String rating = restaurant.getJSONObject("user_rating").getString("aggregate_rating");
            String url = restaurant.getString("url");

            localItemHolder.getItemHolder().setName(name.trim());
            localItemHolder.getItemHolder().setPriceRange(priceRange);
            localItemHolder.getItemHolder().setPhotoUrl(photoUrl);
            localItemHolder.getItemHolder().setAddress(address.trim());
            localItemHolder.getItemHolder().setLocation(city.trim());
            localItemHolder.getItemHolder().setZomatoUrl(rating + ";" + url);

            EndpointsAsyncTask endpointsAsyncTask = new EndpointsAsyncTask(EndpointsAsyncTask
                    .UPDATE);
            endpointsAsyncTask.execute(localItemHolder.getItemHolder());

            EventBus.getDefault().post(new PhotoUpdateEvent(localItemHolder));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
