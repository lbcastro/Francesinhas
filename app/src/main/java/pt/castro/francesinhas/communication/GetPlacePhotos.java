package pt.castro.francesinhas.communication;

/**
 * Created by lourenco on 21/06/15.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.events.PhotoUpdateEvent;
import pt.castro.francesinhas.list.LocalItemHolder;
import pt.castro.francesinhas.list.Param;
import pt.castro.francesinhas.list.PhotoReference;


/**
 * Asynchronous task used to communicate with the backend server.
 * Created by lourenco.castro on 01-04-2015.
 */
public class GetPlacePhotos extends AsyncTask<String, Void, String> {

    public final static int ALL_PHOTOS = 0;
    public final static int PHOTO_REFERENCE = 1;
    private static final String PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/";
    private int mode;
    private LocalItemHolder localItemHolder;

    public GetPlacePhotos(final LocalItemHolder localItemHolder) {
        this.localItemHolder = localItemHolder;
    }

    private static String buildPhotoUrl(String params) {
        String url = String.format(Locale.ENGLISH, "%sphoto?%s", PLACES_API_URL, params);
        url = url.replace(' ', '+');
        return url;
    }

    private static String buildUrl(String method, String params, Param... extraParams) {
        String url = String.format(Locale.ENGLISH, "%s%s/json?%s", "https://maps.googleapis.com/maps/api/place/", method, params);
        url = addExtraParams(url, extraParams);
        url = url.replace(' ', '+');
        return url;
    }

    private static String addExtraParams(String base, Param... extraParams) {
        for (Param param : extraParams) {
            base += "&" + param.name + (param.value != null ? "=" + param.value : "");
        }
        return base;
    }

    public void getAllPhotos(final String url) {
        this.mode = ALL_PHOTOS;
        this.execute(url);
    }

    public void getPhotoReference(final String url) {
        this.mode = PHOTO_REFERENCE;
        this.execute(url);
    }

    protected String doInBackground(String... urls) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final URL url = new URL(urls[0]);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
    protected void onPostExecute(final String string) {
        if (string == null || string.isEmpty()) {
            Log.d("Photos", "Result was null");
            return;
        }
        try {
            Log.d("JsonString", string);
            final JSONObject object = new JSONObject(string);
            JSONObject result = object.getJSONObject("result");

            if (mode == ALL_PHOTOS) {
                JSONArray jsonPhotos = result.optJSONArray("photos");
                List<PhotoReference> photos = new ArrayList<>();
                if (jsonPhotos != null) {
                    for (int i = 0; i < Math.min(jsonPhotos.length(), 10); i++) {
                        JSONObject jsonPhoto = jsonPhotos.getJSONObject(i);
                        String photoReference = jsonPhoto.getString("photo_reference");
                        int width = jsonPhoto.getInt("width"), height = jsonPhoto.getInt("height");
                        photos.add(new PhotoReference(photoReference, width, height));
                    }
                    localItemHolder.setPhotoReferences(photos);
                    PhotoReference reference = null;
                    for (int x = 0; x < Math.min(photos.size(), 10); x++) {
                        if ((reference != null && photos.get(x).getWidth() > reference.getWidth()) || reference == null) {
                            reference = photos.get(x);
                        }
                    }
                    // TODO: Make this value dynamic, adjusting to the current device resolution
                    if (reference == null || reference.getWidth() < 720) {
                        return;
                    }
                    localItemHolder.setPhotoUrl(buildPhotoUrl(String.format("maxwidth=%s&photoreference=%s&key=%s", reference.getWidth(), reference.getReference(), "AIzaSyDSQ408Gts6XQxTEaec8b38sCIMSQWuoc4")));
                } else {
                    Log.d("Photos", "No photos found");
                }
                EventBus.getDefault().post(new PhotoUpdateEvent(localItemHolder));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}