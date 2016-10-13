package com.lxj.sample.letsplay.Main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lxj.sample.letsplay.MyViews.CustomImageView;
import com.lxj.sample.letsplay.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context context;
    private List data;
    private OnItemClickListener onItemClickListener;

    public FriendsRecyclerViewAdapter(Context context,List data){
        this.context = context;
        this.data = data;
    }
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = LayoutInflater.from(context).inflate(R.layout.item_friends,parent,false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER){
            View view = LayoutInflater.from(context).inflate(R.layout.item_foot,parent,false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            //holder.tv.setText(data.get(position));
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItemCount() == position + 1){
            return TYPE_FOOTER;
        } else{
            return TYPE_ITEM;
        }
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder {

        CustomImageView userImage;
        TextView username;
        TextView sign;
        public ItemViewHolder(View view) {
            super(view);
            userImage = (CustomImageView) view.findViewById(R.id.friends_user_image);
            username = (TextView) view.findViewById(R.id.friends_username);
            sign = (TextView) view.findViewById(R.id.friends_sign);

        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}
