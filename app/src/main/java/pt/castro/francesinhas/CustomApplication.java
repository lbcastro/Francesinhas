package pt.castro.francesinhas;

import android.app.Application;

/**
 * Created by lourenco on 10/01/16.
 */
public class CustomApplication extends Application {

    private static PlacesManager mPlacesManager;

    public static PlacesManager getPlacesManager() {
        return mPlacesManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlacesManager = new PlacesManager();
    }
}
