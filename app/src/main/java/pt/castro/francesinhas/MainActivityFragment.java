package pt.castro.francesinhas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.list.CustomRecyclerViewAdapter;
import pt.castro.francesinhas.list.DividerItemDecoration;

public class MainActivityFragment extends Fragment {

    @InjectView(R.id.fragment_main_recycler_view) RecyclerView mRecyclerView;

    private View mFragmentView;
    private CustomRecyclerViewAdapter mCustomRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, mFragmentView);
        mCustomRecyclerViewAdapter = new CustomRecyclerViewAdapter();
        mRecyclerView.setAdapter(mCustomRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return mFragmentView;
    }

    public void setItems(List<ItemHolder> items) {
        mCustomRecyclerViewAdapter.setItems(items);
    }
}