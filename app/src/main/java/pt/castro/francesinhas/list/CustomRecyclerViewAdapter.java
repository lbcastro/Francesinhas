package pt.castro.francesinhas.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.beautifulparallax.ParallaxViewController;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    private static final String CACHED_EMPTY_BITMAP = "empty";
    private ParallaxViewController parallaxViewController;
    private List<LocalItemHolder> items;
    private List<LocalItemHolder> visibleItems;
    private boolean votingEnabled;
    private Bitmap emptyBitmap;

    public CustomRecyclerViewAdapter(final Context context) {
        EventBus.getDefault().register(this);
        items = new ArrayList<>();
        generateEmptyBitmap(context);
        parallaxViewController = new ParallaxViewController();
    }

    private void generateEmptyBitmap(final Context context) {
        final File imageFile = ImageLoader.getInstance().getDiskCache().get
                (CACHED_EMPTY_BITMAP);
        if (imageFile == null || !imageFile.exists()) {
            emptyBitmap = BitmapFactory.decodeResource(context.getResources(), R
                    .drawable.francesinha_blur);
            try {
                ImageLoader.getInstance().getDiskCache().save(CACHED_EMPTY_BITMAP,
                        emptyBitmap);
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

    public void setItems(List<LocalItemHolder> items) {
        this.items = items;
        flushFilter();
    }

    public void add(LocalItemHolder itemHolder) {
        items.add(itemHolder);
        flushFilter();
    }

    public void clear() {
        items.clear();
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
        parallaxViewController.imageParallax(viewHolder.imageView);
        return viewHolder;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ItemHolder itemHolder = visibleItems.get(position).getItemHolder();

        final ImageLoader imageLoader = ImageLoader.getInstance();
        final ImageViewAware aware = new ImageViewAware(holder.imageView, false);
        if (itemHolder.getPhotoUrl() != null && !itemHolder.getPhotoUrl().equals("n/a")) {
            imageLoader.cancelDisplayTask(aware);
            imageLoader.displayImage(itemHolder.getPhotoUrl(), aware, PhotoUtils
                    .getDisplayImageOptions(true));
        } else {
            holder.imageView.setImageBitmap(emptyBitmap);
        }

        final String text = Integer.toString(items.indexOf(visibleItems.get(position))
                + 1);
        holder.rankingTextView.setText(text);
        holder.titleTextView.setText(itemHolder.getName());
        holder.locationTextView.setText(itemHolder.getLocation());
        holder.votesUp.setText(Integer.toString(itemHolder.getVotesUp()));
        holder.votesDown.setText(Integer.toString(itemHolder.getVotesDown()));
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
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.INVISIBLE :
                    View.VISIBLE);
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