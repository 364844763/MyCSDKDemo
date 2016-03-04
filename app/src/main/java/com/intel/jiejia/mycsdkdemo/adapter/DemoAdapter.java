package com.intel.jiejia.mycsdkdemo.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.intel.jiejia.mycsdkdemo.R;
import com.intel.jiejia.mycsdkdemo.activity.MainActivity;
import com.intel.jiejia.mycsdkdemo.bean.DemoInfo;
import com.intel.jiejia.mycsdkdemo.service.MyService;
import com.intel.jiejia.mycsdkdemo.utils.SharedPreferencesUtils;

import java.util.List;

/**
 * Created by jiejia on 1/25/2016.
 */
public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> {
    private List<DemoInfo> mDatas;
    private Context mContext;
    private MyService.MyBinder builder;
    public DemoAdapter(Context mContext, List<DemoInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public DemoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.demo_item, parent,
                false));
    }

    @Override
    public void onBindViewHolder(DemoAdapter.ViewHolder holder, final int position) {
        final DemoInfo bean = mDatas.get(position);
        holder.mTextView.setText(bean.getName());
        holder.mDTextView.setText(bean.getDescripition());
        boolean tap = (boolean) SharedPreferencesUtils.getParam(mContext,bean.getName(),false);
        holder.mSwitch.setChecked(tap);
        if (tap){
//            Intent intent = new Intent(mContext, mDatas.get(position).getService());
//            mContext.startService(intent);
            if (position!=1){
                Intent intent = new Intent(mContext, mDatas.get(position).getService());
                mContext.startService(intent);
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, mDatas.get(position).getClz());
                        mContext.startActivity(intent);
                    }
                }, 100);

            }

        }else {
            Class service=mDatas.get(position).getClz();
            if (service!=null){
            Intent stopIntent = new Intent(mContext, service);
            mContext.stopService(stopIntent);}
        }
        holder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setParam(mContext,mDatas.get(position).getName(),isChecked);
                if (isChecked){
                    if (mDatas.get(position).getClz()==null){
                        Intent intent = new Intent(mContext, mDatas.get(position).getService());
                        mContext.startService(intent);
                    }else {
                        Intent intent = new Intent(mContext, mDatas.get(position).getClz());
                        mContext.startActivity(intent);
                    }

                }else {
                    Class service=mDatas.get(position).getClz();
                if (service==null){
                    return;}

                Intent stopIntent = new Intent(mContext, service);
                mContext.stopService(stopIntent);
                }
            }
        });

    }
    private class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            builder = (MyService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {


        }

    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Switch mSwitch;
        TextView mTextView;
        TextView mDTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mSwitch = (Switch) itemView.findViewById(R.id.sw_demo);
            mTextView = (TextView) itemView.findViewById(R.id.tv_demo_name);
            mDTextView = (TextView) itemView.findViewById(R.id.tv_demo_description);
        }
    }
}
