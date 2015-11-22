package pt.castro.francesinhas.list;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
            imageLoader.displayImage(itemHolder.getPhotoUrl(), aware, PhotoUtils
                    .getDisplayImageOptions());
        } else {
            holder.imageView.setImageDrawable(null);
            holder.imageView.setBackgroundColor(holder.imageView.getContext().getResources().getColor(R.color.blue_light, null));
        }
        final String text = Integer.toString(items.indexOf(visibleItems.get(position)) + 1);
        holder.rankingTextView.setText(text);
        holder.titleTextView.setText(itemHolder.getName());
        holder.subtitleTextView.setText(itemHolder.getLocation());
        holder.votesUp.setText(Integer.toString(itemHolder.getVotesUp()));
        holder.votesDown.setText(Integer.toString(itemHolder.getVotesDown()));
        holder.votesParent.setVisibility(View.GONE);
//        holder.votesParent.setTranslationX(holder.votesParent.getContext().getResources().getDimension(R.dimen.button_size));
        holder.textParent.setTranslationX(0);
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
        @InjectView(R.id.votes_parent)
        LinearLayout votesParent;
        @InjectView(R.id.text_parent)
        RelativeLayout textParent;

        private boolean voting;
        private boolean translated;

        private ObjectAnimator votesAnimator;
        private TranslateAnimation textAnimator;

        public ViewHolder(View itemView) {
            super(itemView);
            EventBus.getDefault().register(this);
            ButterKnife.inject(this, itemView);
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

        @OnClick(R.id.custom_row_clickable)
        void onClickRow() {
            translate();
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
            EventBus.getDefault().post(userClickEvent);
        }

        public void cancelAnimations() {
            if (votesAnimator != null) {
                votesAnimator.cancel();
            }
            if (textAnimator != null) {
                textAnimator.cancel();
            }
        }

        private void translate() {
            float end = votesParent.getContext().getResources().getDimension(R.dimen.button_size);
            final float start = translated ? 0 : -end;
            end = translated ? -end : end;

//            votesAnimator = ObjectAnimator.ofFloat(votesParent, "translationX", translated ? -end : 0, translated ? start : end);
//            votesAnimator.setDuration(500);
//            votesAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//            votesAnimator.start();

            votesParent.setVisibility(translated ? View.VISIBLE : View.GONE);

            textAnimator = new TranslateAnimation(start, translated ? end : 0, textParent.getTranslationY(), textParent.getTranslationY());
            textAnimator.setDuration(500);
            textAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            textAnimator.setFillAfter(true);
//            textParent.startAnimation(textAnimator);
            imageView.startAnimation(textAnimator);

            translated = !translated;
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
            voting = false;
        }
    }
}