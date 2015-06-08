package pt.castro.francesinhas.list;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
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
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PlacePickerEvent());
            }
        });
        final AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar((Toolbar) fragmentView.findViewById(R.id.toolbar));
        final ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        return fragmentView;
    }

    public void setItems(final List<ItemHolder> items) {
        recyclerViewAdapter.setItems(items);
        if (mainRecyclerView.getAdapter() == null) {
            mainRecyclerView.setAdapter(recyclerViewAdapter);
        }
    }
}