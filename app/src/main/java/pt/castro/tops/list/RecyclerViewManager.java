package pt.castro.tops.list;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.JsonMap;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.tops.R;
import pt.castro.tops.events.EventBusHook;
import pt.castro.tops.list.decoration.CustomItemDecoration;

/**
 * Created by lourenco on 19/01/16.
 */
public class RecyclerViewManager {

    private UltimateRecyclerView mainRecyclerView;
    private CustomRecyclerViewAdapter recyclerViewAdapter;

    private boolean refreshing;

    private static int getVote(UserHolder userHolder, String itemId) {
        if (userHolder == null) {
            return 0;
        }
        JsonMap map = userHolder.getVotes();
        if (map == null || map.get(itemId) == null) {
            return 0;
        }
        final BigDecimal vote = (BigDecimal) map.get(itemId);
        return vote != null ? vote.intValueExact() : 0;
    }

    public void resume() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void stop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void add(final LocalItemHolder localItemHolder) {
        if (mainRecyclerView.getAdapter() == null) {
            mainRecyclerView.setAdapter(recyclerViewAdapter);
        }
        if (refreshing) {
            mainRecyclerView.hideEmptyView();
            refreshing = false;
        }
        recyclerViewAdapter.add(localItemHolder);
    }

    public void setRecyclerView(final Context context, final UltimateRecyclerView recyclerView) {
        recyclerViewAdapter = new CustomRecyclerViewAdapter();
        mainRecyclerView = recyclerView;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mainRecyclerView.setLayoutManager(layoutManager);
        mainRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    Picasso.with(context).pauseTag(recyclerViewAdapter);
                } else {
                    Picasso.with(context).resumeTag(recyclerViewAdapter);
                }
            }
        });
        mainRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventBus.getDefault().post(new ERequestRefresh());
            }
        });
        mainRecyclerView.setDefaultSwipeToRefreshColorScheme(ContextCompat.getColor(context, R
                .color.blue_bright));
        mainRecyclerView.setEmptyView(R.layout.empty_list, UltimateRecyclerView.EMPTY_CLEAR_ALL);
        mainRecyclerView.hideEmptyView();
        mainRecyclerView.setLoadMoreView(R.layout.custom_bottom_progressbar);
        mainRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                EventBus.getDefault().post(new ELoadMore());
            }
        });
        mainRecyclerView.addItemDecoration(new CustomItemDecoration());
        mainRecyclerView.setAdapter(recyclerViewAdapter);

        mainRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                setRefreshing(true);
                mainRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setEmptyList(final String message) {
        recyclerViewAdapter.clear();
        final TextView emptyText = (TextView) mainRecyclerView.findViewById(R.id.empty_text);
        emptyText.setText(message);
        mainRecyclerView.showEmptyView();
    }

    public void setConnected(final boolean enabled) {
        if (enabled) {
            mainRecyclerView.enableDefaultSwipeRefresh(true);
        } else {
            recyclerViewAdapter.clear();
            setEmptyList(mainRecyclerView.getContext().getString(R.string.not_connected));
            mainRecyclerView.enableDefaultSwipeRefresh(false);
        }
    }

    public void setRefreshing(final boolean refreshing) {
        mainRecyclerView.enableDefaultSwipeRefresh(true);
        mainRecyclerView.setRefreshing(refreshing);
    }

    public void clear() {
        recyclerViewAdapter.clear();
        recyclerViewAdapter.reset();
        mainRecyclerView.scrollVerticallyToPosition(0);
    }

    public void setFilter(final String filter) {
        recyclerViewAdapter.setFilter(filter);
    }

    public void setVoting(final boolean enabled) {
        recyclerViewAdapter.setVoting(enabled);
    }

    @EventBusHook
    public void onEvent(final ERequestRefresh listRefreshEvent) {
        refreshing = true;
    }

    public class ELoadMore {

    }
}