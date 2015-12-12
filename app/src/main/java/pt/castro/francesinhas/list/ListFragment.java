//package pt.castro.francesinhas.list;
//
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.Fragment;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import de.greenrobot.event.EventBus;
//import pt.castro.francesinhas.R;
//import pt.castro.francesinhas.events.list.ListRefreshEvent;
//import pt.castro.francesinhas.events.place.PlacePickerEvent;
//
//public class ListFragment extends Fragment {
//
//    @Bind(R.id.fragment_recycler_view)
//    UltimateRecyclerView mainRecyclerView;
//    @Bind(R.id.floating_action_button)
//    FloatingActionButton floatingActionButton;
//    @Bind(R.id.toolbar)
//    Toolbar toolbar;
//
//    private CustomRecyclerViewAdapter recyclerViewAdapter;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
//            savedInstanceState) {
//        final View fragmentView = inflater.inflate(R.layout.fragment_list, container,
//                false);
//        ButterKnife.bind(this, fragmentView);
//        recyclerViewAdapter = new CustomRecyclerViewAdapter();
//        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mainRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout
//                .OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                EventBus.getDefault().post(new ListRefreshEvent(false));
//            }
//        });
//        mainRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
//                    ImageLoader.getInstance().pause();
//                } else {
//                    ImageLoader.getInstance().resume();
//                }
//            }
//        });
//        final AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
//        appCompatActivity.setSupportActionBar(toolbar);
//        final ActionBar actionBar = appCompatActivity.getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(false);
//        }
//        return fragmentView;
//    }
//
//    @OnClick(R.id.floating_action_button)
//    void onClickFloatingActionButton() {
//        EventBus.getDefault().post(new PlacePickerEvent());
//    }
//
//    public void setItems(final List<LocalItemHolder> items) {
//        recyclerViewAdapter.setItems(items);
//        if (mainRecyclerView.getAdapter() == null) {
//            mainRecyclerView.setAdapter(recyclerViewAdapter);
//        }
//    }
//
//    public void setEmptyList(final String message) {
//        setItems(new ArrayList<LocalItemHolder>());
//        final TextView emptyText = (TextView) mainRecyclerView.findViewById(R.id
//                .empty_text);
//        emptyText.setText(message);
//    }
//
//    public void setButton(final boolean enabled) {
//        floatingActionButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
//    }
//
//    public void setFilter(final String filter) {
//        recyclerViewAdapter.setFilter(filter);
//    }
//
//    public void setVoting(final boolean enabled) {
//        recyclerViewAdapter.setVoting(enabled);
//    }
//}