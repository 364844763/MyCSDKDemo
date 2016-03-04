package com.intel.jiejia.mycsdkdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.intel.jiejia.mycsdkdemo.R;
import com.intel.jiejia.mycsdkdemo.bean.MyResolveInfo;

import java.util.List;

/**
 * Created by jiejia on 1/14/2016.
 */
public class AppsAdapter extends BaseAdapter {
    private List<MyResolveInfo> mList;
    private Context mContext;

    public AppsAdapter(Context mContext, List<MyResolveInfo> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setData(List<MyResolveInfo> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyResolveInfo info = mList.get(position);
        boolean is = info.is();
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.app_item, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.tv_app_name);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            holder.choice = (CheckBox) convertView.findViewById(R.id.cb_app_choice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.choice.setClickable(false);
        holder.choice.setChecked(is);
        holder.icon.setImageDrawable(info.activityInfo.loadIcon(mContext.getApplicationContext().getPackageManager()));
        holder.text.setText(info.activityInfo.loadLabel(mContext.getPackageManager()).toString());
        return convertView;
    }

    private static class ViewHolder {
        TextView text;
        ImageView icon;
        CheckBox choice;
    }
}
