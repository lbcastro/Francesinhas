package pt.castro.francesinhas.list;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.EventBusHook;
import pt.castro.francesinhas.events.place.PhotoUpdateEvent;
import pt.castro.francesinhas.events.place.ScoreChangeEvent;
import pt.castro.francesinhas.events.user.UserClickEvent;
import pt.castro.francesinhas.tools.PhotoUtils;

/**
 * Created by lourenco.castro on 07/05/15.
 */
public class CustomRecyclerViewAdapter extends RecyclerView
        .Adapter<CustomRecyclerViewAdapter.ViewHolder> {

//    CustomParallaxViewController parallaxViewController;

    private List<LocalItemHolder> items;
    private List<LocalItemHolder> visibleItems;
    private boolean votingEnabled;

    public CustomRecyclerViewAdapter() {
        EventBus.getDefault().register(this);
        items = Collections.emptyList();
//        parallaxViewController = new CustomParallaxViewController();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
//        parallaxViewController.registerImageParallax(recyclerView);
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
            } else if (localItemHolder.getItemHolder().getLocation().toLowerCase()
                    .contains(query.toLowerCase())) {
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
        final View row = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .row_main, parent, false);
        ViewHolder viewHolder = new ViewHolder(row);
//        parallaxViewController.imageParallax(viewHolder.imageView);
        return viewHolder;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cancelAnimations();
        final ItemHolder itemHolder = visibleItems.get(position).getItemHolder();
        if (itemHolder.getPhotoUrl() != null && !itemHolder.getPhotoUrl().equals("n/a")) {
            final ImageLoader imageLoader = ImageLoader.getInstance();
            final ImageViewAware aware = new ImageViewAware(holder.imageView, false);
            imageLoader.cancelDisplayTask(aware);
            imageLoader.displayImage(itemHolder.getPhotoUrl(), aware, PhotoUtils.getDisplayImageOptions(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason
                        failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap
                        loadedImage) {
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else {
            holder.imageView.setImageDrawable(null);
            holder.imageView.setBackgroundColor(holder.imageView.getContext()
                    .getResources().getColor(R.color.blue_light));
        }
        final String text = Integer.toString(items.indexOf(visibleItems.get(position))
                + 1);
        holder.rankingTextView.setText(text);
        holder.titleTextView.setText(itemHolder.getName());
        holder.votesUp.setText(Integer.toString(itemHolder.getVotesUp()));
        holder.votesDown.setText(Integer.toString(itemHolder.getVotesDown()));
        holder.translated = true;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.custom_row_ranking)
        TextView rankingTextView;
        @Bind(R.id.custom_row_name)
        TextView titleTextView;
        @Bind(R.id.backdrop_image)
        ImageView imageView;
        @Bind(R.id.backdrop_clickable)
        View clickable;
        @Bind(R.id.votes_up)
        TextView votesUp;
        @Bind(R.id.votes_down)
        TextView votesDown;
        @Bind(R.id.clickable_parent)
        RelativeLayout clickableParent;

        private boolean voting;
        private boolean translated;

        private ObjectAnimator textAnimator;

        public ViewHolder(View itemView) {
            super(itemView);
            EventBus.getDefault().register(this);
            ButterKnife.bind(this, itemView);
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

        private void clickVote(int vote) {
            if (!voting) {
                if (!votingEnabled) {
                    postClick();
                } else {
                    voting = true;
                    setSelected(vote);
                    postVote(getAdapterPosition(), vote);
                }
            }
        }

        private void setSelected(int vote) {
            switch (vote) {
                case 1:
                    votesUp.setSelected(!votesUp.isSelected());
                    votesDown.setSelected(false);
                    break;
                case -1:
                    votesUp.setSelected(false);
                    votesDown.setSelected(!votesDown.isSelected());
                    break;
                default:
                    votesUp.setSelected(false);
                    votesDown.setSelected(false);
                    break;
            }
        }

        @OnClick(R.id.votes_up)
        void onClickVotesUp() {
            clickVote(1);
        }

        @OnClick(R.id.votes_down)
        void onClickVotesDown() {
            clickVote(-1);
        }

        @OnClick(R.id.backdrop_clickable)
        void onClickRow() {
            postClick(getAdapterPosition());
        }

        // FIXME: This manipulation should not occur inside the adapter
        private void postVote(int position, int vote) {
            final LocalItemHolder localItemHolder = visibleItems.get(position);
            final ItemHolder itemHolder = localItemHolder.getItemHolder();
            // TODO: Review this interaction, it might be too fragile

            int votesUp = itemHolder.getVotesUp();
            int votesDown = itemHolder.getVotesDown();

            switch (vote) {
                case -1:
                    if (localItemHolder.getUserVote() == 1) {
                        votesUp--;
                    } else if (localItemHolder.getUserVote() == -1) {
                        votesDown--;
                        break;
                    }
                    votesDown++;
                    break;
                case 1:
                    if (localItemHolder.getUserVote() == 1) {
                        votesUp--;
                        break;
                    } else if (localItemHolder.getUserVote() == -1) {
                        votesDown--;
                    }
                    votesUp++;
                    break;
            }
            itemHolder.setVotesUp(votesUp);
            itemHolder.setVotesDown(votesDown);

            this.votesUp.setText(Integer.toString(votesUp));
            this.votesDown.setText(Integer.toString(votesDown));

            if (vote == localItemHolder.getUserVote()) {
                localItemHolder.setUserVote(0);
            } else {
                localItemHolder.setUserVote(vote);
            }

            final UserClickEvent userClickEvent = new UserClickEvent(localItemHolder);
            userClickEvent.setUserVote(vote);
            userClickEvent.setView(titleTextView);
            EventBus.getDefault().post(userClickEvent);
        }

        public void cancelAnimations() {
            if (textAnimator != null) {
                textAnimator.cancel();
            }
        }

        private void postClick(int position) {
            final LocalItemHolder localItemHolder = visibleItems.get(position);
            UserClickEvent userClickEvent = new UserClickEvent(localItemHolder);
            userClickEvent.setView(itemView);
            EventBus.getDefault().post(userClickEvent);
        }

        private void postClick() {
            EventBus.getDefault().post(new UserClickEvent(null));
        }

        @EventBusHook
        public void onEvent(ScoreChangeEvent scoreChangeEvent) {
            voting = false;
        }
    }
}