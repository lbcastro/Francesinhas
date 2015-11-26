package pt.castro.francesinhas.list;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.events.ListRefreshEvent;
import pt.castro.francesinhas.events.PlacePickerEvent;

public class ListFragment extends Fragment {

    @InjectView(R.id.fragment_recycler_view)
    SuperRecyclerView mainRecyclerView;
    @InjectView(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;
    private CustomRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.inject(this, fragmentView);
        recyclerViewAdapter = new CustomRecyclerViewAdapter();
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventBus.getDefault().post(new ListRefreshEvent(false));
            }
        });
        mainRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    ImageLoader.getInstance().pause();
                } else {
                    ImageLoader.getInstance().resume();
                }
            }
        });
        final AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar((Toolbar) fragmentView.findViewById(R.id.toolbar));
        final ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        return fragmentView;
    }

    @OnClick(R.id.floating_action_button)
    void onClickFloatingActionButton() {
        EventBus.getDefault().post(new PlacePickerEvent());
    }

    public void setItems(final List<LocalItemHolder> items) {
        recyclerViewAdapter.setItems(items);
        if (mainRecyclerView.getAdapter() == null) {
            mainRecyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    public void setFilter(final String filter) {
        recyclerViewAdapter.setFilter(filter);
    }

    public void setVoting(final boolean enabled) {
        recyclerViewAdapter.setVoting(enabled);
    }
}