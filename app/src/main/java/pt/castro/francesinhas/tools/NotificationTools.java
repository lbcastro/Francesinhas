package pt.castro.francesinhas.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lourenco on 09/06/15.
 */
public class NotificationTools {

    private static Toast sActiveToast;

    private static void showToast(final Context context, final String text, final int duration) {
        if (sActiveToast != null) {
            sActiveToast.cancel();
        }
        sActiveToast = Toast.makeText(context, text, duration);
        sActiveToast.show();
    }

    private static void showToast(final Context context, final int textResource, final int duration) {
        if (sActiveToast != null) {
            sActiveToast.cancel();
        }
        sActiveToast = Toast.makeText(context, textResource, duration);
        sActiveToast.show();
    }

    public static void toastLoggedVote(final Context context) {
        showToast(context, "You need to be logged in to vote", Toast.LENGTH_SHORT);
    }

    public static void toastLoggedAdd(final Context context) {
        showToast(context, "You need to be logged in to add a new place", Toast.LENGTH_LONG);
    }

    public static void toastLoginFailed(final Context context) {
        showToast(context, "Login failed\nPlease try again", Toast.LENGTH_LONG);
    }

    public static void toastGoogleConnectionFailed(final Context context) {
        showToast(context, "Connection to Google Play failed", Toast.LENGTH_LONG);
    }

    public static void toastCustomText(final Context context, final String text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    public static void toastCustomText(final Context context, final int textResource) {
        showToast(context, textResource, Toast.LENGTH_SHORT);
    }
}