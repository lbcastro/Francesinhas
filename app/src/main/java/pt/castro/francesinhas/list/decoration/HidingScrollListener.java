package pt.castro.francesinhas.list.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;

import pt.castro.francesinhas.R;

public abstract class HidingScrollListener implements NestedScrollView
        .OnScrollChangeListener {

    private int mToolbarOffset = 0;
    private int mToolbarHeight;

    public HidingScrollListener(Context context) {
        mToolbarHeight = getToolbarHeight(context);
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes
                (new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int
            oldScrollX, int oldScrollY) {
        int dy = scrollY - oldScrollY;
        Log.d("Hide", "" + dy);

        clipToolbarOffset();
        onMoved(mToolbarOffset, dy);

        if ((mToolbarOffset < mToolbarHeight && dy > 0) || (mToolbarOffset > 0 && dy <
                0)) {
            mToolbarOffset += dy;
        }
    }

    private void clipToolbarOffset() {
        if (mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if (mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }

    public abstract void onMoved(int toolbarDistance, int variation);
}