package pt.castro.francesinhas.list.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.github.florent37.beautifulparallax.ParallaxViewController;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lourenco on 25/11/15.
 */
public class CustomParallaxViewController extends ParallaxViewController {
    protected List<ImageView> imageViewList = new ArrayList<>();
    int PARALLAX_SPEED = 100;
    float actionBarHeight;
    View firstVisibleView = null;
    float recyclerviewCenterY = -1;
    Rect rect = new Rect();
    View currentImageView;

    public static float limit(float min, float value, float max) {
        return Math.max(Math.min(value, max), min);
    }

    public void registerImageParallax(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(this);
        TypedValue tv = new TypedValue();
        Context context = recyclerView.getContext();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
    }

    public void imageParallax(ImageView imageView) {
        if (!imageViewList.contains(imageView))
            imageViewList.add(imageView);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getChildCount() > 0) {
            if (firstVisibleView == null) firstVisibleView = recyclerView.getChildAt(0);
            if (recyclerviewCenterY == -1)
                recyclerviewCenterY = recyclerView.getMeasuredHeight() / 2 + recyclerView.getTop();

            for (int i = 0, count = imageViewList.size(); i < count; ++i) {
                currentImageView = imageViewList.get(i);
                currentImageView.getGlobalVisibleRect(rect);

                float yOffset = limit(-1, (recyclerviewCenterY - rect.top - actionBarHeight) / currentImageView.getHeight(), 1);
                ViewHelper.setTranslationY(currentImageView, (-1f + yOffset) * PARALLAX_SPEED);
            }
        }
    }
}