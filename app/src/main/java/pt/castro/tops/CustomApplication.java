package pt.castro.tops;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import de.greenrobot.event.EventBus;
import pt.castro.tops.tools.NotificationUtils;

/**
 * Created by lourenco on 10/01/16.
 */
@ReportsCrashes(
        formUri = "https://lbcastro.cloudant.com/acra-tops/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "thedishentiouststsenintl",
        formUriBasicAuthPassword = "7b643152ccebfd5ff8520f884000825b7f565629",
        formKey = "", // This is required for backward compatibility but not used
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION, ReportField.PACKAGE_NAME, ReportField.REPORT_ID,
                ReportField.BUILD, ReportField.STACK_TRACE},
        mode = ReportingInteractionMode.SILENT,
        resNotifText = R.string.place_exists)
public class CustomApplication extends Application {

    private static PlacesManager mPlacesManager;
    private static UsersManager mUsersManager;

    public static PlacesManager getPlacesManager() {
        return mPlacesManager;
    }

    public static UsersManager getUsersManager() {
        return mUsersManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        LeakCanary.install(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Picasso picasso = new Picasso.Builder(this).indicatorsEnabled(true).memoryCache(new
                LruCache(5000000)).build();
        Picasso.setSingletonInstance(picasso);
        mPlacesManager = new PlacesManager();
        mUsersManager = new UsersManager();
        EventBus.clearCaches();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NotificationUtils.clear();
        EventBus.clearCaches();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
