package pt.castro.tops.list;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.beautifulparallax.ParallaxViewController;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.marshalchen.ultimaterecyclerview.animators.internal.ViewHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.tops.CustomApplication;
import pt.castro.tops.R;
import pt.castro.tops.events.EventBusHook;
import pt.castro.tops.events.place.PhotoUpdateEvent;
import pt.castro.tops.events.place.ScoreChangeEvent;
import pt.castro.tops.events.user.UserClickEvent;
import pt.castro.tops.list.decoration.RoundedCornersTransformation;
import pt.castro.tops.list.decoration.WrapWidthTextView;

/**
 * Created by lourenco.castro on 07/05/15.
 */
public class CustomRecyclerViewAdapter extends UltimateViewAdapter<CustomRecyclerViewAdapter
        .ViewHolder> {

    private final List<LocalItemHolder> visibleItems;
    private ParallaxViewController parallaxViewController;
    private boolean votingEnabled;
    private int mLastPosition;

    public CustomRecyclerViewAdapter() {
        EventBus.getDefault().register(this);
        visibleItems = new ArrayList<>();
        parallaxViewController = new ParallaxViewController();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parallaxViewController.registerImageParallax(recyclerView);
    }

    @EventBusHook
    public void onEvent(final PhotoUpdateEvent photoUpdateEvent) {
        notifyItemChanged(visibleItems.indexOf(photoUpdateEvent.getLocalItemHolder()));
    }

    public void add(LocalItemHolder localItemHolder) {
        synchronized (visibleItems) {
            if (!visibleItems.contains(localItemHolder)) {
                visibleItems.add(localItemHolder);
                notifyItemInserted(visibleItems.indexOf(localItemHolder));
            }
        }
    }

    public void clear() {
        synchronized (visibleItems) {
            mLastPosition = 0;
            visibleItems.clear();
            notifyDataSetChanged();
        }
    }

    public void reset() {
        mLastPosition = 0;
        flushFilter();
    }

    private void flushFilter() {
        synchronized (visibleItems) {
            final List<LocalItemHolder> tempList = CustomApplication.getPlacesManager().getList();
            visibleItems.clear();
            visibleItems.addAll(tempList);
            notifyDataSetChanged();
        }
    }

    public void setFilter(String query) {
        synchronized (visibleItems) {
            final List<LocalItemHolder> tempList = CustomApplication.getPlacesManager().getList();
            visibleItems.clear();
            for (LocalItemHolder localItemHolder : tempList) {
                if (localItemHolder.getItemHolder().getName().toLowerCase().contains(query
                        .toLowerCase())) {
                    visibleItems.add(localItemHolder);
                } else if (localItemHolder.getItemHolder().getLocation().toLowerCase().contains
                        (query.toLowerCase())) {
                    visibleItems.add(localItemHolder);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setVoting(boolean enabled) {
        votingEnabled = enabled;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(row);
        parallaxViewController.imageParallax(viewHolder.imageView);
        return viewHolder;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main,
                parent, false);
        return new ViewHolder(view);
    }

    protected Animator[] getAnimators(View view) {
        return new Animator[]{ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight
                (), 0)};
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int adapterPosition = holder.getAdapterPosition();
        final ItemHolder itemHolder = visibleItems.get(adapterPosition).getItemHolder();
        if (itemHolder.getPhotoUrl() != null && !itemHolder.getPhotoUrl().equals("n/a")) {
            if (holder.imageView.getTag() == null || !holder.imageView.getTag().equals(itemHolder
                    .getPhotoUrl())) {
                Uri uri = Uri.parse(itemHolder.getPhotoUrl());

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    Picasso.with(holder.imageView.getContext()).load(uri).tag(this).into(holder
                            .imageView);
                } else {
                    Picasso.with(holder.imageView.getContext()).load(uri).transform(new
                            RoundedCornersTransformation(40, 0)).tag(this).into(holder.imageView);
                }
                holder.imageView.setTag(itemHolder.getPhotoUrl());
            }
        } else {
            Picasso.with(holder.imageView.getContext()).load(R.drawable.francesinha_blur).into
                    (holder.imageView);
        }
        holder.rankingTextView.setText(String.format(Locale.getDefault(), "%s", position + 1));
        String name = itemHolder.getName();
        float distance = visibleItems.get(adapterPosition).getDistance();
        if (distance == -1) {
            name = name + "<font color='gray'> " + itemHolder.getLocation().trim() +
                    "" +
                    "</font>";
        } else {
            name = name + "<font color='#bdbdbd'> " + String.format(Locale.getDefault(), "%skm",
                    distance) + "</font>";
        }
        holder.titleTextView.setText(Html.fromHtml(name), TextView.BufferType.SPANNABLE);


        holder.votesUp.setText(String.format(Locale.getDefault(), "%s", itemHolder.getVotesUp()));
        holder.votesDown.setText(String.format(Locale.getDefault(), "%s", itemHolder.getVotesDown
                ()));
        switch (visibleItems.get(adapterPosition).getUserVote()) {
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

        if (adapterPosition > mLastPosition) {
            for (Animator anim : getAnimators(holder.itemView)) {
                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(300).start();
            }
            mLastPosition = adapterPosition;
        } else {
            ViewHelper.clear(holder.itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        try {
            return visibleItems.size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    @Override
    public int getAdapterItemCount() {
        return visibleItems.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.custom_row_ranking)
        TextView rankingTextView;
        @Bind(R.id.custom_row_name)
        WrapWidthTextView titleTextView;
        @Bind(R.id.backdrop_image)
        ImageView imageView;
        @Bind(R.id.backdrop_clickable)
        View clickable;
        @Bind(R.id.votes_up)
        TextView votesUp;
        @Bind(R.id.votes_down)
        TextView votesDown;
        @Bind(R.id.votes_up_indicator)
        View votesUpIndicator;
        @Bind(R.id.votes_down_indicator)
        View votesDownIndicator;
        @Bind(R.id.card_parent)
        CardView cardView;

        private boolean voting;

        public ViewHolder(View itemView) {
            super(itemView);
            EventBus.getDefault().register(this);
            ButterKnife.bind(this, itemView);
        }

        private void setVoteUpSelected() {
            votesUpIndicator.setVisibility(View.VISIBLE);
            votesDownIndicator.setVisibility(View.INVISIBLE);
        }

        private void setVoteDownSelected() {
            votesUpIndicator.setVisibility(View.INVISIBLE);
            votesDownIndicator.setVisibility(View.VISIBLE);
        }

        private void resetVotes() {
            votesUpIndicator.setVisibility(View.INVISIBLE);
            votesDownIndicator.setVisibility(View.INVISIBLE);
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
                    toggleVisibility(votesUpIndicator);
                    votesDownIndicator.setVisibility(View.INVISIBLE);
                    break;
                case -1:
                    votesUpIndicator.setVisibility(View.INVISIBLE);
                    toggleVisibility(votesDownIndicator);
                    break;
                default:
                    resetVotes();
                    break;
            }
        }

        private void toggleVisibility(final View view) {
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.INVISIBLE : View
                    .VISIBLE);
        }

        @OnClick(R.id.votes_up_clickable)
        void onClickVotesUp() {
            clickVote(1);
        }

        @OnClick(R.id.votes_down_clickable)
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

            this.votesUp.setText(String.format(Locale.getDefault(), "%s", itemHolder.getVotesUp()));
            this.votesDown.setText(String.format(Locale.getDefault(), "%s", itemHolder
                    .getVotesDown()));

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