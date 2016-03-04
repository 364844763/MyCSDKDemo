package com.intel.jiejia.mycsdkdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intel.jiejia.mycsdkdemo.R;
import com.intel.jiejia.mycsdkdemo.activity.MainActivity;
import com.intel.jiejia.mycsdkdemo.bean.DemoInfo;

import java.util.List;

/**
 * Created by jiejia on 1/19/2016.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    private List<DemoInfo> mDatas;
    private Context mContext;

    public HomeAdapter(Context mContext, List<DemoInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.demo_adapter, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final HomeAdapter.MyViewHolder holder, int position) {
        DemoInfo bean = mDatas.get(position);
        holder.description.setText(bean.getDescripition());
        holder.title.setText(bean.getName());
        holder.imageView.setImageResource(bean.getIconId());
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView description;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_demo_icon);
            title = (TextView) itemView.findViewById(R.id.tv_demo_title);
            description = (TextView) itemView.findViewById(R.id.iv_description);
        }
    }
}
