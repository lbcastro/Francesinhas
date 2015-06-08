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
import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.EventBusHook;
import pt.castro.francesinhas.events.ScoreChangeEvent;
import pt.castro.francesinhas.events.UserClickEvent;

/**
 * Created by lourenco.castro on 07/05/15.
 */
public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter
        .ViewHolder> {

    private List<LocalItemHolder> items = Collections.emptyList();

    public CustomRecyclerViewAdapter() {
    }

    public void setItems(List<LocalItemHolder> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main,
                parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemHolder itemHolder = items.get(position).getItemHolder();
        holder.rankingTextView.setText(Integer.toString(position + 1));
        holder.titleTextView.setText(itemHolder.getName());
        holder.subtitleTextView.setText(itemHolder.getLocation());
        holder.imageView.setBackgroundColor(itemHolder.getBackgroundColor());
        holder.votesUp.setText(Integer.toString(itemHolder.getVotesUp()));
        holder.votesDown.setText(Integer.toString(itemHolder.getVotesDown()));
        switch (items.get(position).getUserVote()) {
            case -1:
                holder.votesDown.setSelected(true);
                break;
            case 1:
                holder.votesUp.setSelected(true);
                break;
        }
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
        private boolean clicking;

        public ViewHolder(View itemView) {
            super(itemView);
            EventBus.getDefault().register(this);
            ButterKnife.inject(this, itemView);
            clickable.setOnClickListener(this);
            votesUp.setOnClickListener(this);
            votesDown.setOnClickListener(this);
        }

        private void setVoteUpSelected() {
            votesUp.setSelected(true);
            votesDown.setSelected(false);
        }

        private void setVoteDownSelected() {
            votesUp.setSelected(false);
            votesDown.setSelected(true);
        }

        @Override
        public void onClick(View v) {
            if (clicking) {
                return;
            }
            final ItemHolder itemHolder = items.get(getAdapterPosition()).getItemHolder();
            final UserClickEvent userClickEvent = new UserClickEvent(itemHolder);
            switch (v.getId()) {
                case R.id.custom_row_votes_up:
                    clicking = true;
                    userClickEvent.setUserVote(1);
//                    new EndpointsAsyncTask(EndpointsAsyncTask.INCREASE).execute(itemHolder);
//                    itemHolder.setVotesUp(itemHolder.getVotesUp() + 1);
//                    notifyDataSetChanged();
                    EventBus.getDefault().post(userClickEvent);
                    setVoteUpSelected();
                    break;
                case R.id.custom_row_votes_down:
                    clicking = true;
                    userClickEvent.setUserVote(-1);
                    EventBus.getDefault().post(userClickEvent);
                    setVoteDownSelected();
//                    new EndpointsAsyncTask(EndpointsAsyncTask.DECREASE).execute(itemHolder);
//                    itemHolder.setVotesDown(itemHolder.getVotesDown() + 1);
//                    notifyDataSetChanged();
                    break;
                case R.id.custom_row_clickable:
                    break;
            }
        }

        @EventBusHook
        public void onEvent(ScoreChangeEvent scoreChangeEvent) {
            clicking = false;
        }
    }
}