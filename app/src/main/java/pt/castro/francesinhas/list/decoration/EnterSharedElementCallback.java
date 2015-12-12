package pt.castro.francesinhas.list.decoration;

import android.annotation.TargetApi;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import pt.castro.francesinhas.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class EnterSharedElementCallback extends SharedElementCallback {
    private static final String TAG = "EnterSharedElementCallback";

    private final float mStartTextSize;
    private final float mEndTextSize;

    public EnterSharedElementCallback(Context context) {
        Resources res = context.getResources();
        mStartTextSize = res.getDimensionPixelSize(R.dimen.text_size_m);
        mEndTextSize = res.getDimensionPixelSize(R.dimen.text_size_l);
    }

    @Override
    public void onSharedElementStart(List<String> sharedElementNames, List<View>
            sharedElements, List<View> sharedElementSnapshots) {
        TextView textView = (TextView) sharedElements.get(2);

        // Setup the TextView's start values.
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mStartTextSize);
    }

    @Override
    public void onSharedElementEnd(List<String> sharedElementNames, List<View>
            sharedElements, List<View> sharedElementSnapshots) {
        TextView textView = (TextView) sharedElements.get(2);

        // Record the TextView's old width/height.
        int oldWidth = textView.getMeasuredWidth();
        int oldHeight = textView.getMeasuredHeight();

        // Setup the TextView's end values.
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEndTextSize);

        // Re-measure the TextView (since the text size has changed).
        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec
                .UNSPECIFIED);
        textView.measure(widthSpec, heightSpec);

        // Record the TextView's new width/height.
        int newWidth = textView.getMeasuredWidth();
        int newHeight = textView.getMeasuredHeight();

        // Layout the TextView in the center of its container, accounting for its new
        // width/height.
        int widthDiff = newWidth - oldWidth;
        int heightDiff = newHeight - oldHeight;
        textView.layout(textView.getLeft() - widthDiff / 2, textView.getTop() -
                heightDiff / 2, textView.getRight() + widthDiff / 2, textView.getBottom
                () + heightDiff / 2);
    }
}