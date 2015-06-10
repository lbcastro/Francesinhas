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
    private boolean votingEnabled;

    public CustomRecyclerViewAdapter() {
    }

    public void setItems(List<LocalItemHolder> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setVoting(boolean enabled) {
        votingEnabled = enabled;
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
        holder.votesUp.setText(Integer.toString(itemHolder.getVotesUp()));
        holder.votesDown.setText(Integer.toString(itemHolder.getVotesDown()));
        switch (items.get(position).getUserVote()) {
            case -1:
                holder.setVoteDownSelected();
                break;
            case 1:
                holder.setVoteUpSelected();
                break;
            default:
                holder.resetVotes();
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

        private void resetVotes() {
            votesUp.setSelected(false);
            votesDown.setSelected(false);
        }

        @Override
        public void onClick(View v) {
            if (clicking) {
                return;
            }
            switch (v.getId()) {
                case R.id.custom_row_votes_up:
                    if (!votingEnabled) {
                        postClick();
                        break;
                    }
                    // This prevents multiple votes on the same item.
                    if (votesUp.isSelected()) {
                        break;
                    }
                    clicking = true;
                    postVote(getAdapterPosition(), 1);
                    notifyDataSetChanged();
                    break;
                case R.id.custom_row_votes_down:
                    if (!votingEnabled) {
                        postClick();
                        break;
                    }
                    // This prevents multiple votes on the same item.
                    if (votesDown.isSelected()) {
                        break;
                    }
                    clicking = true;
                    postVote(getAdapterPosition(), -1);
                    notifyDataSetChanged();
                    break;
                case R.id.custom_row_clickable:
                    break;
            }
        }

        private void postVote(int position, int vote) {
            final LocalItemHolder localItemHolder = items.get(position);
            final ItemHolder itemHolder = localItemHolder.getItemHolder();
            localItemHolder.setUserVote(vote);
            switch (vote) {
                case -1:
                    itemHolder.setVotesDown(itemHolder.getVotesDown() + 1);
                    break;
                case 1:
                    itemHolder.setVotesUp(itemHolder.getVotesUp() + 1);
                    break;
            }
            final UserClickEvent userClickEvent = new UserClickEvent(itemHolder);
            userClickEvent.setUserVote(vote);
            EventBus.getDefault().post(userClickEvent);
        }

        private void postClick() {
            EventBus.getDefault().post(new UserClickEvent(null));
        }

        @EventBusHook
        public void onEvent(ScoreChangeEvent scoreChangeEvent) {
            clicking = false;
        }
    }
}