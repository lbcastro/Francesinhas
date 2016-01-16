package pt.castro.francesinhas.list.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import pt.castro.francesinhas.R;

/**
 * Created by lourenco on 13/01/16.
 */
public class CustomItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final int itemCount = state.getItemCount();

        if (itemCount > 0 && itemPosition == itemCount - 1) {
            outRect.set(0, 0, 0, (int) view.getResources().getDimension(R.dimen
                    .margin_s));
        }
    }
}
