package pt.castro.tops.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    public static void setTransparentStatusBar(Window window) {
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

    public static CardView generateCardView(final Context context, final ViewGroup parent, final
    String label, final int drawableResource, final String content) {
        final CardView cardView = (CardView) LayoutInflater.from(context).inflate(R.layout
                .details_card, parent, false);
        final TextView labelView = (TextView) cardView.findViewById(R.id.details_card_label);
        labelView.setText(label);
        labelView.setCompoundDrawablesWithIntrinsicBounds(PhotoUtils.tintedDrawable(context,
                drawableResource), null, null, null);
        final TextView contentView = (TextView) cardView.findViewById(R.id.details_card_content);
        contentView.setText(content, TextView.BufferType.SPANNABLE);
        return cardView;
    }

    public static LinearLayout generateDetailsLinear(final Context context, final ViewGroup
            parent, final String label, final int drawableResource, final String content) {
        final LinearLayout linearL = (LinearLayout) LayoutInflater.from(context).inflate(R.layout
                .details_linear, parent, false);
        final TextView labelView = (TextView) linearL.findViewById(R.id.details_linear_label);
        labelView.setText(label);
        labelView.setCompoundDrawablesWithIntrinsicBounds(PhotoUtils.tintedDrawable(context,
                drawableResource), null, null, null);
        final TextView contentView = (TextView) linearL.findViewById(R.id.details_linear_content);
        contentView.setText(content, TextView.BufferType.SPANNABLE);
        return linearL;
    }

    public static void addStarImage(final Context context, final ViewGroup parent, final int
            drawableResource) {
        final ImageView star = (ImageView) LayoutInflater.from(context).inflate(R.layout
                .rating_star, parent, false);
        star.setBackgroundResource(drawableResource);
        parent.addView(star);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void setTransparentStatusBar(final Activity activity) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View
                    .SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static boolean hasSoftKeys(final Activity activity) {
        boolean hasSoftwareKeys;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = activity.getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else {
            boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }

    public static int dpToPx(Context context, float dp) {
        float d = context.getResources().getDisplayMetrics().density;
        int margin = (int) (dp * d); // margin in pixels
        return margin;
    }

    public static void setToolbarCollapsible(final Toolbar toolbar, final boolean collapsible) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(collapsible ? AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
                AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP : 0);
        toolbar.setCollapsible(collapsible);
    }

    public static void setImmersiveMode(final Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static boolean isKitkatOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isLollipopOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int getStatusBarHeight(final Resources resources) {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }
}