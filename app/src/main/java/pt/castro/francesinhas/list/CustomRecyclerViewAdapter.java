package pt.castro.francesinhas.list;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.beautifulparallax.ParallaxViewController;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.marshalchen.ultimaterecyclerview.animators.internal.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.CustomApplication;
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
public class CustomRecyclerViewAdapter extends UltimateViewAdapter<CustomRecyclerViewAdapter
        .ViewHolder> {

    private static final String CACHED_EMPTY_BITMAP = "empty";

    private ParallaxViewController parallaxViewController;
    private List<LocalItemHolder> visibleItems;
    private boolean votingEnabled;
    private Bitmap emptyBitmap;
    private int mLastPosition;

    public CustomRecyclerViewAdapter(final Context context) {
        EventBus.getDefault().register(this);
        visibleItems = new ArrayList<>();
        generateEmptyBitmap(context);
        parallaxViewController = new ParallaxViewController();
    }

    private void generateEmptyBitmap(final Context context) {
        final File imageFile = ImageLoader.getInstance().getDiskCache().get(CACHED_EMPTY_BITMAP);
        if (imageFile == null || !imageFile.exists()) {
            emptyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable
                    .francesinha_blur);
            try {
                ImageLoader.getInstance().getDiskCache().save(CACHED_EMPTY_BITMAP, emptyBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            emptyBitmap = PhotoUtils.bitmapFromFile(imageFile);
        }
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
        if (!visibleItems.contains(localItemHolder)) {
            visibleItems.add(localItemHolder);
            notifyItemInserted(visibleItems.indexOf(localItemHolder));
        }
    }

    public void clear() {
        mLastPosition = 0;
        visibleItems = new ArrayList<>();
        notifyDataSetChanged();
//        flushFilter();
    }

    public void reset() {
        mLastPosition = 0;
        flushFilter();
    }

    private void flushFilter() {
        final List<LocalItemHolder> tempList = CustomApplication.getPlacesManager().getList();
        visibleItems = new ArrayList<>();
        visibleItems.addAll(tempList);
        notifyDataSetChanged();
    }

    public void setFilter(String query) {
        final List<LocalItemHolder> tempList = CustomApplication.getPlacesManager().getList();
        visibleItems = new ArrayList<>();
        for (LocalItemHolder localItemHolder : tempList) {
            if (localItemHolder.getItemHolder().getName().toLowerCase().contains(query
                    .toLowerCase())) {
                visibleItems.add(localItemHolder);
            } else if (localItemHolder.getItemHolder().getLocation().toLowerCase().contains(query
                    .toLowerCase())) {
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
            Bitmap cachedBitmap = PhotoUtils.getCachedBitmap(itemHolder.getPhotoUrl());
            if (cachedBitmap != null) {
                holder.imageView.setImageBitmap(cachedBitmap);
            } else if (holder.imageView.getTag() == null || !holder.imageView.getTag().equals
                    (itemHolder.getPhotoUrl())) {
                final ImageLoader imageLoader = ImageLoader.getInstance();
                final ImageViewAware aware = new ImageViewAware(holder.imageView, false);
                imageLoader.cancelDisplayTask(aware);
                imageLoader.displayImage(itemHolder.getPhotoUrl(), aware, PhotoUtils
                        .getDisplayImageOptions(true), new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.bottomShadow.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        holder.imageView.setImageBitmap(emptyBitmap);
                        holder.bottomShadow.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.bottomShadow.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        holder.imageView.setImageBitmap(emptyBitmap);
                        holder.bottomShadow.setVisibility(View.VISIBLE);
                    }
                });
                holder.imageView.setTag(itemHolder.getPhotoUrl());
            }
        } else {
            holder.imageView.setImageBitmap(emptyBitmap);
            holder.bottomShadow.setVisibility(View.VISIBLE);
        }
        holder.rankingTextView.setText(Integer.toString(position + 1));
        holder.titleTextView.setText(itemHolder.getName());
        holder.locationTextView.setText(itemHolder.getLocation().trim());
        holder.votesUp.setText(Integer.toString(itemHolder.getVotesUp()));
        holder.votesDown.setText(Integer.toString(itemHolder.getVotesDown()));
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
        TextView titleTextView;
        @Bind(R.id.custom_row_location)
        TextView locationTextView;
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
        @Bind(R.id.custom_row_bottom_shadow)
        View bottomShadow;

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