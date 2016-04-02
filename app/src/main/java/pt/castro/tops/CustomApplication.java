package pt.castro.tops;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import de.greenrobot.event.EventBus;

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
        mode = ReportingInteractionMode.SILENT)
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
        Picasso picasso = new Picasso.Builder(this).indicatorsEnabled(true).memoryCache(new
                LruCache(5000000)) // Maybe something fishy here?
                .build();
        Picasso.setSingletonInstance(picasso);
        mPlacesManager = new PlacesManager();
        mUsersManager = new UsersManager();
        EventBus.clearCaches();
    }
}
