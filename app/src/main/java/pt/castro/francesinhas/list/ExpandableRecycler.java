package pt.castro.francesinhas.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

import pt.castro.francesinhas.R;

/**
 * Created by lourenco on 14/11/15.
 */
public class ExpandableRecycler extends ExpandableRecyclerAdapter<CustomParentViewHolder, CustomChildViewHolder> {

    private LayoutInflater mInflater;

    /**
     * Primary constructor. Sets up {@link #mParentItemList} and {@link #mItemList}.
     * <p/>
     * Changes to {@link #mParentItemList} should be made through add/remove methods in
     * {@link ExpandableRecyclerAdapter}
     *
     * @param parentItemList List of all {@link ParentListItem} objects to be
     *                       displayed in the RecyclerView that this
     *                       adapter is linked to
     */
    public ExpandableRecycler(Context context, List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public CustomParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = mInflater.inflate(R.layout.row_main, parentViewGroup, false);
        return new CustomParentViewHolder(view);
    }

    @Override
    public CustomChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = mInflater.inflate(R.layout.row_expanded, childViewGroup, false);
        return new CustomChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(CustomParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        LocalItemHolder localItemHolder = (LocalItemHolder) parentListItem;
        parentViewHolder.bind(localItemHolder.getItemHolder());
    }

    @Override
    public void onBindChildViewHolder(CustomChildViewHolder childViewHolder, int position, Object childListItem) {

    }
}
