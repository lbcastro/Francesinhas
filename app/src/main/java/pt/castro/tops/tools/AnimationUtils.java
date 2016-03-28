package pt.castro.tops.tools;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

/**
 * Created by lourenco on 28/03/16.
 */
public class AnimationUtils {

    public static AnimationSet getSlideBottomAnimation(float startY, float endY, float startA,
                                                       float endA, int duration) {
        final AnimationSet set = new AnimationSet(true);
        Animation progressTranslate = new TranslateAnimation(0, 0, startY, endY);
        progressTranslate.setDuration(duration);
        progressTranslate.setFillAfter(true);
        set.addAnimation(progressTranslate);
        Animation progressAlpha = new AlphaAnimation(startA, endA);
        progressAlpha.setDuration(100);
        progressAlpha.setFillAfter(true);
        set.addAnimation(progressAlpha);
        set.setInterpolator(new AccelerateInterpolator());
        set.setFillAfter(true);
        set.setDuration(duration);
        return set;
    }

}
