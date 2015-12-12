package pt.castro.francesinhas.list.decoration;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;

import pt.castro.francesinhas.R;

/**
 * Created by lourenco on 07/12/15.
 */
public class TransitionUtils {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Transition makeSharedElementEnterTransition(Context context) {
        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);

        Transition changeBounds = new ChangeBounds();
//        changeBounds.addTarget(R.id.logo_white);
        changeBounds.addTarget(R.id.backdrop_image);
        changeBounds.addTarget(R.id.backdrop_clickable);
        changeBounds.setDuration(300);
        set.addTransition(changeBounds);

//        Transition textSize = new TextSizeTransition();
//        textSize.addTarget(R.id.logo_white);
//        set.addTransition(textSize);

        return set;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Transition makeSlideTransition() {
        Transition slide = new Slide();
        slide.excludeTarget(R.id.appbar, true);
        slide.excludeTarget(R.id.toolbar, true);
//        slide.excludeTarget(R.id.collapsing_toolbar, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        return slide;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Transition makeFadeTransition() {
        Transition fade = new Fade();
        fade.excludeTarget(R.id.nested_scroll, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        return fade;
    }
}
