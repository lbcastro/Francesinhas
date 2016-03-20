package pt.castro.tops.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.Random;

import pt.castro.tops.R;

/**
 * Created by lourenco.castro on 02-06-2015.
 */
public class LayoutUtils {

    public static int getRandomColor(final Context context) {
        int[] colors = context.getResources().getIntArray(R.array.colors);
        return colors[randInt(0, colors.length - 1)];
    }

    public static int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static void expand(final View v) {
        Log.d("LayoutUtils", "Expanding");
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int targetHeight = v.getMeasuredHeight() * 4;
        final int initialHeight = v.getMeasuredHeight();
        Log.d("LayoutUtils", "initial " + initialHeight + ", target " + targetHeight);
        v.getLayoutParams().height = initialHeight;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? targetHeight : (int) ((
                        (targetHeight - initialHeight) * interpolatedTime) + initialHeight);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics()
                .density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        Log.d("LayoutUtils", "Collapsing");
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight *
                            interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics()
                .density));
        v.startAnimation(a);
    }

    public static void setTranslucentStatusBar(Window window) {
        if (window == null) return;
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatusBarLollipop(window);
        } else if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatusBarKiKat(window);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setTranslucentStatusBarLollipop(Window window) {
        window.setStatusBarColor(window.getContext().getResources().getColor(android.R.color
                .transparent));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setTranslucentStatusBarKiKat(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
}