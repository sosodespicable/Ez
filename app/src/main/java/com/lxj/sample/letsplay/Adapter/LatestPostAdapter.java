package com.lxj.sample.letsplay.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lxj.sample.letsplay.Interfaces.OnRecyclerViewOnClickListener;
import com.lxj.sample.letsplay.MyViews.CustomImageView;
import com.lxj.sample.letsplay.R;
import com.lxj.sample.letsplay.bean.LatestPost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/6 0006.
 */
public class LatestPostAdapter extends RecyclerView.Adapter<LatestPostAdapter.LatestItemViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private List<LatestPost> list = new ArrayList<>();
    private OnRecyclerViewOnClickListener mListener;

    public LatestPostAdapter(Context context,List<LatestPost> list){
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public LatestItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_latest,parent,false);
        return new LatestItemViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(LatestItemViewHolder holder, int position) {
        LatestPost item = list.get(position);
        if (item.getFirstImage() == null){
            holder.itemImg.setBackgroundResource(R.drawable.no_img);
            Log.v("isSetImageResource","设置了");
        } else {
            Glide.with(context)
                    .load(item.getFirstImage())
                    .error(R.drawable.no_img)
                    .centerCrop()
                    .into(holder.itemImg);
            Log.v("isSetImageResource","设置了");
        }
    holder.latestNewsTitle.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }


    public class LatestItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView itemImg;
        private TextView latestNewsTitle;
        private OnRecyclerViewOnClickListener listener;

        public LatestItemViewHolder(View itemView, OnRecyclerViewOnClickListener listener) {
            super(itemView);
            itemImg = (ImageView) itemView.findViewById(R.id.latest_image);
            latestNewsTitle = (TextView) itemView.findViewById(R.id.latest_title);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null){
                listener.OnItemClick(v,getLayoutPosition());
            }
        }
    }
}
