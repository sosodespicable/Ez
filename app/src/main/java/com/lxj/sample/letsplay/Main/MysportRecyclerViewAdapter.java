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
import java.util.Map;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class MysportRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context context;
    private List<Map<String,Object>> data;
    private OnItemClickListener onItemClickListener;

    public MysportRecyclerViewAdapter(Context context,List data){
        this.context = context;
        this.data = data;
    }
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view,int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = LayoutInflater.from(context).inflate(R.layout.item_my_sports,parent,false);
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
        TextView title;
        TextView type;
        TextView time;

        public ItemViewHolder(View view) {
            super(view);
            userImage = (CustomImageView) view.findViewById(R.id.mysport_user_image);
            title = (TextView) view.findViewById(R.id.mysport_title_textview);
            type = (TextView) view.findViewById(R.id.mysport_type_textview);
            time = (TextView) view.findViewById(R.id.mysport_time_textview);

        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}
