package com.lxj.sample.letsplay.Main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lxj.sample.letsplay.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class FragmentTheme extends Fragment {

    private RecyclerView recyclerView;
    private MysportRecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    private List<Map<String,Object>> data = new ArrayList<>();
    boolean isLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.mysport_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mysport_swipeRefreshLayout);
        adapter = new MysportRecyclerViewAdapter(getActivity(),data);
        initView();
        initData();
        return view;
    }
    public void initView(){
        swipeRefreshLayout.setColorSchemeResources(R.color.hot);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        getData();
                    }
                },2000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()){
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing){
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading){
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        adapter.setOnItemClickListener(new MysportRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }
    public void initData(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        },1500);
    }
    public void getData(){
        for(int i = 0;i < 10;i++){
            Map<String,Object> map = new HashMap<>();
            data.add(map);
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyItemRemoved(adapter.getItemCount());
    }
}
