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
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.communication.EndpointsAsyncTask;

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
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemHolder itemHolder = items.get(position);
        holder.rankingTextView.setText(Integer.toString(items.get(position).getRanking()));
        holder.titleTextView.setText(items.get(position).getName());
        holder.subtitleTextView.setText(items.get(position).getLocation());
        holder.imageView.setImageResource(items.get(position).getImageResource());
        holder.votesUp.setText(Integer.toString(itemHolder.getVotesUp()));
        holder.votesDown.setText(Integer.toString(itemHolder.getVotesDown()));
    }

    @Override
    public int getItemCount() {
        try {
            return items.size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.custom_row_ranking)
        TextView rankingTextView;
        @InjectView(R.id.custom_row_name)
        TextView titleTextView;
        @InjectView(R.id.custom_row_location)
        TextView subtitleTextView;
        @InjectView(R.id.custom_row_image)
        ImageView imageView;
        @InjectView(R.id.custom_row_clickable)
        View clickable;
        @InjectView(R.id.custom_row_votes_up)
        TextView votesUp;
        @InjectView(R.id.custom_row_votes_down)
        TextView votesDown;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
//            clickable.setOnClickListener(this);
            votesUp.setOnClickListener(this);
            votesDown.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final ItemHolder itemHolder = items.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.custom_row_votes_up:
                    new EndpointsAsyncTask(EndpointsAsyncTask.INCREASE).execute(itemHolder);
                    itemHolder.setVotesUp(itemHolder.getVotesUp() + 1);
                    notifyDataSetChanged();
                    break;
                case R.id.custom_row_votes_down:
                    new EndpointsAsyncTask(EndpointsAsyncTask.DECREASE).execute(itemHolder);
                    itemHolder.setVotesDown(itemHolder.getVotesDown() + 1);
                    notifyDataSetChanged();
                    break;
            }
        }
    }
}
