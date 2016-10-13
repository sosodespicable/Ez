package com.lxj.sample.letsplay.Main;

import android.app.DatePickerDialog;
import android.app.usage.NetworkStats;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lxj.sample.letsplay.Activity.ZhiHuReadActivity;
import com.lxj.sample.letsplay.Adapter.LatestPostAdapter;
import com.lxj.sample.letsplay.DatabaseHelper;
import com.lxj.sample.letsplay.Interfaces.OnRecyclerViewOnClickListener;
import com.lxj.sample.letsplay.NetWorkState;
import com.lxj.sample.letsplay.R;
import com.lxj.sample.letsplay.ZhiHu.ZhiHu_API;
import com.lxj.sample.letsplay.bean.LatestPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class FragmentLatest extends Fragment implements View.OnTouchListener,GestureDetector.OnGestureListener{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    private List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
//    private MyRecyclerViewAdapter adapter;
//    private FloatingActionButton fab;
    private RequestQueue queue;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private SharedPreferences sp;
    boolean isLoading;
    private List<LatestPost> list = new ArrayList<LatestPost>();
    private LatestPostAdapter latestPostAdapter;
    private LinearLayoutManager linearLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    private GestureDetector gestureDetector;

    //2013.5.20知乎日报首次上线
    private int year = 2013;
    private int month = 5;
    private int day = 20;

    //用于记录加载更多的次数
    private int groupCount = -1;
    private final String TAG = "LatestFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linearLayoutManager = new LinearLayoutManager(getActivity());

        gestureDetector = new GestureDetector(this);

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        databaseHelper = new DatabaseHelper(getActivity(),"History.db",null,3);
        db = databaseHelper.getWritableDatabase();
        sp = getActivity().getSharedPreferences("user_setting",Context.MODE_PRIVATE);

        deleteTimeoutPosts();
        //获取当前日期的前一天
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-1);

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest, container, false);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.main_latest_coordinatorLayout);
        coordinatorLayout.setOnTouchListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerview);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_latest_swipeRefreshLayout);
//        fab = (FloatingActionButton) view.findViewById(R.id.fab);
//        fab.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));
        //设置下拉刷新的按钮的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置手指在屏幕上下拉多少距离开始刷新
        swipeRefreshLayout.setDistanceToTriggerSync(300);
        //设置下拉刷新按钮的背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        //设置下拉刷新按钮的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        list.clear();

        if (!NetWorkState.networkConnected(getActivity())){
            showNoNetwork();
            loadFromDB();
        } else{
            load(null);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!list.isEmpty()){
                    list.clear();
                }
                latestPostAdapter.notifyDataSetChanged();
                if (!NetWorkState.networkConnected(getActivity())){
                    showNoNetwork();
                    loadFromDB();
                } else {
                    load(null);
                }

                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH,-1);

                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                groupCount = -1;
            }
        });

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final com.rey.material.app.DatePickerDialog dialog = new com.rey.material.app.DatePickerDialog(getActivity());
//                //初始化被选中的日期
//                dialog.date(day,month,year);
//
//                Calendar c = Calendar.getInstance();
//                //设置最小日期为2013.5.20,如果传入的日期小于19，报错
//                c.set(2013,5,20);
//                //设置最大日期，其中最大日期为当前日期的前一天
//                dialog.dateRange(c.getTimeInMillis(),Calendar.getInstance().getTimeInMillis() - 24*60*60*1000);
//                dialog.show();
//
//                dialog.positiveAction("确定");
//                dialog.negativeAction("取消");
//
//                dialog.positiveActionClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        year = dialog.getYear();
//                        month = dialog.getMonth();
//                        day = dialog.getDay();
//
//                        load(parseDate(dialog.getDate()));
//
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.negativeActionClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//            }
//        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //当不滚动时
                int lastVisibleItem = manager.findLastVisibleItemPosition();
                int totalItemCount = manager.getItemCount();

                if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast){
                    loadMore();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return view;
    }

    private void deleteTimeoutPosts(){
        Calendar c= Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-2);
        String[] whereArgs = {parseDate(c.getTimeInMillis())};
    }
    /**
     * 将long类date转换为String类型
     * @param date date
     * @return String date
     */
    private String parseDate(long date){

        String sDate;
        Date d = new Date(date + 24*60*60*1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        sDate = format.format(d);

        return sDate;
    }

    public void showNoNetwork(){
        Snackbar.make(coordinatorLayout,"当前没有网络连接...",Snackbar.LENGTH_INDEFINITE)
                .setAction("去设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).show();
    }

    private void loadFromDB(){
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        Cursor cursor = db.query("LatestPosts",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                List<String> list = new ArrayList<String>();
                list.add(cursor.getString(cursor.getColumnIndex("img_url")));
                String id = String.valueOf(cursor.getInt(cursor.getColumnIndex("id")));
                String type = String.valueOf(cursor.getInt(cursor.getColumnIndex("type")));
                if ((title != null) && (list.get(0) != null) && (!id.equals("")) && (!type.equals(""))){
                    LatestPost item = new LatestPost(title,list,type,id);
                    this.list.add(item);
                }

            }while (cursor.moveToNext());
        }
        cursor.close();

        latestPostAdapter = new LatestPostAdapter(getActivity(),list);
        recyclerView.setAdapter(latestPostAdapter);
        latestPostAdapter.setOnItemClickListener(new OnRecyclerViewOnClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ZhiHuReadActivity.class);
                intent.putExtra("id",list.get(position).getId());
                intent.putExtra("title",list.get(position).getTitle());
                startActivity(intent);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    //用于加载最新的日报或者历史日报
    public void load(final String date){
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        String url;
        if (date == null){
            url = ZhiHu_API.LATEST;
        } else {
            url = ZhiHu_API.HISTORY + date;
        }

        if (!list.isEmpty()){
            list.clear();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    if (!jsonObject.getString("date").isEmpty()) {
                        JSONArray array = jsonObject.getJSONArray("stories");

                        for (int i = 0; i < array.length(); i++) {
                            JSONArray images = array.getJSONObject(i).getJSONArray("images");
                            String id = array.getJSONObject(i).getString("id");
                            String type = array.getJSONObject(i).getString("type");
                            String title = array.getJSONObject(i).getString("title");
                            List<String> stringList = new ArrayList<String>();
                            for (int j = 0; j < images.length(); j++) {
                                String imgUrl = images.getString(j);
                                stringList.add(imgUrl);
                            }
                            LatestPost item = new LatestPost(title, stringList, type, id);

                            list.add(item);

                            if (!queryIDExists("LatestPosts", id)) {
                                ContentValues values = new ContentValues();
                                values.put("id", Integer.valueOf(id));
                                values.put("title", title);
                                values.put("type", type);
                                values.put("img_url", stringList.get(0));

                                if (date == null) {
                                    String d = jsonObject.getString("date");
                                    values.put("date", Integer.valueOf(d));
                                    storeContent(id, d);
                                } else {
                                    values.put("date", Integer.valueOf(date));
                                        storeContent(id, date);
                                }

                                db.insert("LatestPosts", null, values);
                                values.clear();
                            }
                        }
                    }

                    latestPostAdapter = new LatestPostAdapter(getActivity(), list);
                    recyclerView.setAdapter(latestPostAdapter);
                    latestPostAdapter.setOnItemClickListener(new OnRecyclerViewOnClickListener() {
                        @Override
                        public void OnItemClick(View v, int position) {
                            Intent intent = new Intent(getActivity(), ZhiHuReadActivity.class);
                            intent.putExtra("id", list.get(position).getId());
                            intent.putExtra("title", list.get(position).getTitle());
                            startActivity(intent);
                        }
                    });
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: 刷新到这里");
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (swipeRefreshLayout.isRefreshing()){
                    Snackbar.make(coordinatorLayout,"发生了一些错误",Snackbar.LENGTH_SHORT).show();
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });

        request.setTag(TAG);
        queue.add(request);
    }

    private boolean queryIDExists(String tableName,String id){
        Cursor cursor = db.query(tableName,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                if (id.equals(String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))))){
                    return true;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }

    //将指定id的内容存入数据库中
    private void storeContent(final String id,final String date){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ZhiHu_API.NEWS, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (queryIDExists("LatestPosts", id)) {
                    ContentValues values = new ContentValues();
                    try {
                        if (!jsonObject.isNull("body")) {
                            values.put("id", Integer.valueOf(id));
                            values.put("content", jsonObject.getString("body"));
                            values.put("date", Integer.valueOf(date));
                            db.insert("Contents", null, values);
                            values.clear();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    //加载更多
    private void loadMore(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date(year - 1900,month,day - groupCount);
        final String date = format.format(d);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ZhiHu_API.HISTORY + date, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (!jsonObject.getString("date").isEmpty()) {
                        JSONArray array = jsonObject.getJSONArray("stories");
                        for (int i = 0; i < array.length(); i++) {
                            JSONArray images = array.getJSONObject(i).getJSONArray("images");
                            String id = array.getJSONObject(i).getString("id");
                            String type = array.getJSONObject(i).getString("type");
                            String title = array.getJSONObject(i).getString("title");
                            List<String> stringList = new ArrayList<String>();
                            for (int j = 0; j < images.length(); j++) {
                                String imgUrl = images.getString(j);
                                stringList.add(imgUrl);
                            }

                            LatestPost item = new LatestPost(title, stringList, type, id);

                            list.add(item);

                            if (!queryIDExists("LatestPosts", id)) {
                                ContentValues values = new ContentValues();
                                values.put("id", Integer.valueOf(id));
                                values.put("title", title);
                                values.put("type", Integer.valueOf(type));
                                values.put("img_url", stringList.get(0));

                                if (date == null) {
                                    String d = jsonObject.getString("date");
                                    values.put("date", Integer.valueOf(d));
                                    storeContent(id, d);
                                } else {
                                    values.put("date", Integer.valueOf(date));
                                    storeContent(id, date);
                                }
                                db.insert("LatestPosts", null, values);

                                values.clear();
                            }
                        }
                    }
                    latestPostAdapter.notifyDataSetChanged();

                    groupCount++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (swipeRefreshLayout.isRefreshing()){
                    Snackbar.make(coordinatorLayout,"发生了一些错误！",Snackbar.LENGTH_SHORT).show();
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });

        request.setTag(TAG);
        queue.add(request);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > 50 && Math.abs(velocityX) > 0){
            final com.rey.material.app.DatePickerDialog dialog = new com.rey.material.app.DatePickerDialog(getActivity());
                //初始化被选中的日期
                dialog.date(day,month,year);

                Calendar c = Calendar.getInstance();
                //设置最小日期为2013.5.20,如果传入的日期小于19，报错
                c.set(2013,5,20);
                //设置最大日期，其中最大日期为当前日期的前一天
                dialog.dateRange(c.getTimeInMillis(),Calendar.getInstance().getTimeInMillis() - 24*60*60*1000);
                dialog.show();

                dialog.positiveAction("确定");
                dialog.negativeAction("取消");

                dialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        year = dialog.getYear();
                        month = dialog.getMonth();
                        day = dialog.getDay();

                        load(parseDate(dialog.getDate()));

                        dialog.dismiss();
                    }
                });

                dialog.negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        }
        return false;
    }


//    public void initView(View view){
////        adapter = new MyRecyclerViewAdapter(getActivity(),data);
////        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_light);
////        swipeRefreshLayout.post(new Runnable() {
////            @Override
////            public void run() {
////                swipeRefreshLayout.setRefreshing(true);
////            }
////        });
////        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
////            @Override
////            public void onRefresh() {
////                handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        data.clear();
////                        getData();
////                    }
////                },2000);
////            }
////        });
////        recyclerView.setLayoutManager(layoutManager);
////        recyclerView.setAdapter(adapter);
////        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
////            @Override
////            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
////                super.onScrolled(recyclerView, dx, dy);
////
////                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
////                if(lastVisibleItemPosition + 1 == adapter.getItemCount()){
////                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
////                    if(isRefreshing){
////                        adapter.notifyItemRemoved(adapter.getItemCount());
////                        return;
////                    }
////                    if (!isLoading){
////                        isLoading = true;
////                        handler.postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
////                                getData();
////                                isLoading = false;
////                            }
////                        },1000);
////                    }
////                }
////            }
////
////            @Override
////            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
////                super.onScrollStateChanged(recyclerView, newState);
////
////            }
////        });
////        adapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
////            @Override
////            public void onItemClick(View view, int position) {
////
////            }
////
////            @Override
////            public void onItemLongClick(View view, int position) {
////
////            }
////        });
//    }
//    public void initData(){
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getData();
//            }
//        },1500);
//    }
//    public void getData(){
//        for(int i = 0;i < 10;i++){
//            Map<String,Object> map = new HashMap<>();
//            data.add(map);
//        }
//        adapter.notifyDataSetChanged();
//        swipeRefreshLayout.setRefreshing(false);
//        adapter.notifyItemRemoved(adapter.getItemCount());
//
//    }
}
