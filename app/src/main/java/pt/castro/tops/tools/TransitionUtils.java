package pt.castro.tops.tools;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.Fade;
import android.transition.Transition;

/**
 * Created by lourenco on 07/12/15.
 */
public class TransitionUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Transition makeFadeTransition() {
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        return fade;
    }
}
