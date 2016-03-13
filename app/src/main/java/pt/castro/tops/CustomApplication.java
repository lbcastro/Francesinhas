package pt.castro.tops;

import android.app.Application;

import com.facebook.FacebookSdk;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

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

    public static PlacesManager getPlacesManager() {
        return mPlacesManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mPlacesManager = new PlacesManager();
    }
}
