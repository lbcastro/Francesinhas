package pt.castro.tops.list;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.math.BigDecimal;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.JsonMap;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.tops.events.EventBusHook;
import pt.castro.tops.events.list.ListRefreshEvent;
import pt.castro.tops.list.decoration.CustomItemDecoration;

/**
 * Created by lourenco on 19/01/16.
 */
public class RecyclerViewManager {

    private UltimateRecyclerView mainRecyclerView;
    private CustomRecyclerViewAdapter recyclerViewAdapter;

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
        mainRecyclerView.hideEmptyView();
        if (mainRecyclerView.getAdapter() == null) {
            mainRecyclerView.setAdapter(recyclerViewAdapter);
        }
        recyclerViewAdapter.add(localItemHolder);
    }

    public void setRecyclerView(final Context context, final UltimateRecyclerView recyclerView) {
        mainRecyclerView = recyclerView;
        recyclerViewAdapter = new CustomRecyclerViewAdapter(context);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mainRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventBus.getDefault().post(new ERequestRefresh());
            }
        });
        mainRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    ImageLoader.getInstance().pause();
                } else {
                    ImageLoader.getInstance().resume();
                }
            }
        });
        mainRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                EventBus.getDefault().post(new ELoadMore());
            }
        });
        mainRecyclerView.enableLoadmore();
        recyclerViewAdapter.setCustomLoadMoreView(LayoutInflater.from(context).inflate(pt.castro
                .tops.R.layout
                .custom_progress_bar, mainRecyclerView, false));
        setEmptyList(context.getString(pt.castro.tops.R.string.loading));
        mainRecyclerView.addItemDecoration(new CustomItemDecoration());
    }

    public void setEmptyList(final String message) {
        recyclerViewAdapter.clear();
        mainRecyclerView.setAdapter(null);
        final TextView emptyText = (TextView) mainRecyclerView.findViewById(pt.castro.tops.R.id.empty_text);
        emptyText.setText(message);
        mainRecyclerView.showEmptyView();
    }

    public void setNotConnected() {
        setEmptyList("You're not connected!");
        recyclerViewAdapter.clear();
    }

    public void setFilter(final String filter) {
        recyclerViewAdapter.setFilter(filter);
    }

    public void setVoting(final boolean enabled) {
        recyclerViewAdapter.setVoting(enabled);
    }

    @EventBusHook
    public void onEvent(final ListRefreshEvent listRefreshEvent) {
        if (!listRefreshEvent.isRefreshed()) {
            recyclerViewAdapter.reset();
        }
    }

    public class ELoadMore {

    }

    public class ERequestRefresh {

    }
}
