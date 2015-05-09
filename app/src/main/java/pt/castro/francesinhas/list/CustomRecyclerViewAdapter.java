package pt.castro.francesinhas.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.castro.francesinhas.R;

/**
 * Created by lourenco.castro on 07/05/15.
 */
public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    private List<ItemHolder> items = Collections.emptyList();

    public CustomRecyclerViewAdapter() {
    }

    public void setItems(List<ItemHolder> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main, parent, false);
        final ViewHolder viewHolder = new ViewHolder(row);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.rankingTextView.setText(Integer.toString(items.get(position).getRanking()));
        holder.titleTextView.setText(items.get(position).getName());
        holder.subtitleTextView.setText(items.get(position).getLocation());
        holder.imageView.setImageResource(items.get(position).getImageResource());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Holds view references for the CustomRecyclerViewAdapter class.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
        @InjectView(R.id.custom_row_ranking) TextView rankingTextView;
        @InjectView(R.id.custom_row_name) TextView titleTextView;
        @InjectView(R.id.custom_row_location) TextView subtitleTextView;
        @InjectView(R.id.custom_row_image) ImageView imageView;
    }
}
