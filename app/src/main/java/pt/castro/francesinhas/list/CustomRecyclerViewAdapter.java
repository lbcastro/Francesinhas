package pt.castro.francesinhas.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.EventBusHook;
import pt.castro.francesinhas.events.PhotoUpdateEvent;
import pt.castro.francesinhas.events.ScoreChangeEvent;
import pt.castro.francesinhas.events.UserClickEvent;
import pt.castro.francesinhas.tools.PhotoUtils;

/**
 * Created by lourenco.castro on 07/05/15.
 */
public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter
        .ViewHolder> {

    private List<LocalItemHolder> items;
    private List<LocalItemHolder> visibleItems;
    private boolean votingEnabled;

    public CustomRecyclerViewAdapter() {
        EventBus.getDefault().register(this);
        items = Collections.emptyList();
    }

    public void onEvent(final PhotoUpdateEvent photoUpdateEvent) {
        notifyItemChanged(visibleItems.indexOf(photoUpdateEvent.getLocalItemHolder()));
    }

    public void setItems(List<LocalItemHolder> items) {
        this.items = items;
        flushFilter();
    }

    private void flushFilter() {
        visibleItems = new ArrayList<>();
        visibleItems.addAll(items);
        notifyDataSetChanged();
    }

    public void setFilter(String query) {
        visibleItems = new ArrayList<>();
        for (LocalItemHolder localItemHolder : items) {
            if (localItemHolder.getItemHolder().getName().toLowerCase().contains(query
                    .toLowerCase())) {
                visibleItems.add(localItemHolder);
            } else if (localItemHolder.getItemHolder().getLocation().toLowerCase().contains(query.toLowerCase())) {
                visibleItems.add(localItemHolder);
            }
        }
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ItemHolder itemHolder = visibleItems.get(position).getItemHolder();
        if (itemHolder.getPhotoUrl() != null && !itemHolder.getPhotoUrl().equals("n/a")) {
            final ImageLoader imageLoader = ImageLoader.getInstance();
            final ImageViewAware aware = new ImageViewAware(holder.imageView, false);
            imageLoader.cancelDisplayTask(aware);
            imageLoader.displayImage(itemHolder.getPhotoUrl(), aware, PhotoUtils
                    .getDisplayImageOptions());
        }
        holder.rankingTextView.setText(Integer.toString(items.indexOf(visibleItems.get(position))
                + 1));
        holder.titleTextView.setText(itemHolder.getName());
        // Integer.toString(items.indexOf(visibleItems.get(position)) + 1) + ". " +
        holder.subtitleTextView.setText(itemHolder.getLocation());
//        holder.votesUp.setText(Integer.toString(itemHolder.getVotesUp()));
//        holder.votesDown.setText(Integer.toString(itemHolder.getVotesDown()));
        switch (visibleItems.get(position).getUserVote()) {
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
            return visibleItems.size();
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
        @InjectView(R.id.votes_up)
        TextView votesUp;
        @InjectView(R.id.votes_down)
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
                case R.id.votes_up:
                    if (!votingEnabled) {
                        postClick();
                        break;
                    }
                    clicking = true;
                    postVote(getAdapterPosition(), 1);
                    notifyDataSetChanged();
                    break;
                case R.id.votes_down:
                    if (!votingEnabled) {
                        postClick();
                        break;
                    }
                    clicking = true;
                    postVote(getAdapterPosition(), -1);
                    notifyDataSetChanged();
                    break;
                case R.id.custom_row_clickable:
                    postClick(getAdapterPosition());
                    break;
            }
        }

        // FIXME: This manipulation should not occur inside the adapter
        private void postVote(int position, int vote) {
            final LocalItemHolder localItemHolder = visibleItems.get(position);
            final ItemHolder itemHolder = localItemHolder.getItemHolder();
            // TODO: Review this interaction, it might be too fragile
            switch (vote) {
                case -1:
                    if (localItemHolder.getUserVote() == 1) {
                        itemHolder.setVotesUp(itemHolder.getVotesUp() - 1);
                    } else if (localItemHolder.getUserVote() == -1) {
                        itemHolder.setVotesDown(itemHolder.getVotesDown() - 1);
                        break;
                    }
                    itemHolder.setVotesDown(itemHolder.getVotesDown() + 1);
                    break;
                case 1:
                    if (localItemHolder.getUserVote() == -1) {
                        itemHolder.setVotesDown(itemHolder.getVotesDown() - 1);
                    } else if (localItemHolder.getUserVote() == 1) {
                        itemHolder.setVotesUp(itemHolder.getVotesUp() - 1);
                        break;
                    }
                    itemHolder.setVotesUp(itemHolder.getVotesUp() + 1);
                    break;
            }
            if (vote == localItemHolder.getUserVote()) {
                localItemHolder.setUserVote(0);
            } else {
                localItemHolder.setUserVote(vote);
            }
            final UserClickEvent userClickEvent = new UserClickEvent(localItemHolder);
            userClickEvent.setUserVote(vote);
            EventBus.getDefault().post(userClickEvent);
        }

        private void postClick(int position) {
            final LocalItemHolder localItemHolder = visibleItems.get(position);
            EventBus.getDefault().post(new UserClickEvent(localItemHolder));
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