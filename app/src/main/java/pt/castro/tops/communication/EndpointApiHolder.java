package pt.castro.tops.communication;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import pt.castro.francesinhas.backend.myApi.MyApi;


/**
 * Created by Lourenço on 08/06/2015.
 */
public class EndpointApiHolder {
    private static MyApi myApiService = null;

    public static MyApi getInstance() {
        if (myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new
                    AndroidJsonFactory(), null)
                    .setRootUrl("https://castro-francesinhas.appspot.com/_ah/api/");
            builder.setApplicationName("Francesinhas");
            myApiService = builder.build();
        }
        return myApiService;
    }
}