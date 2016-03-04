package com.intel.jiejia.mycsdkdemo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.intel.jiejia.mycsdkdemo.R;
import com.intel.jiejia.mycsdkdemo.adapter.AppsAdapter;
import com.intel.jiejia.mycsdkdemo.bean.MyResolveInfo;
import com.intel.jiejia.mycsdkdemo.service.MyService;
import com.intel.jiejia.mycsdkdemo.utils.SharedPreferencesUtils;

import java.util.LinkedList;
import java.util.List;

public class TappingActivity extends AppCompatActivity {
    private List<MyResolveInfo> mApps;
    private ListView mAppListView;
    private AppsAdapter mAppsAdapter;
    private Button mButton;
    private MyService.MyBinder builder;
    private MyConn conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapping);
        loadApps();
        initView();
//        startService(new Intent(this, MyService.class));
        conn = new MyConn();
        Intent server = new Intent(this, MyService.class);
        bindService(server, conn, BIND_AUTO_CREATE);
        if (builder==null) {
            startService(new Intent(this, MyService.class));
            Intent server1 = new Intent(this, MyService.class);
            bindService(server1, conn, BIND_AUTO_CREATE);
        }
    }

    private void initView() {
        mAppListView = (ListView) findViewById(R.id.lv_app_list);
        mButton = (Button) findViewById(R.id.btn_confirm);
        mAppsAdapter = new AppsAdapter(this, mApps);
        mAppListView.setAdapter(mAppsAdapter);
        mAppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("jj", "test");
                upFavourApp(position);
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                stopService(new Intent(TappingActivity.this, MyService.class));
//                startService(new Intent(TappingActivity.this, MyService.class));

            }
        });
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = new LinkedList<>();
        for (ResolveInfo info : getPackageManager().queryIntentActivities(mainIntent, 0)) {
//        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
            MyResolveInfo myResolveInfo = new MyResolveInfo(info, false);

            String name = (String) SharedPreferencesUtils.getParam(this, "name", "");
            assert name != null;
            if (name.equals(info.activityInfo.loadLabel(this.getPackageManager()).toString())) {
                myResolveInfo.setIs(true);
            }
            mApps.add(myResolveInfo);
//            myResolveInfo.setIs(false);
        }
    }

    private void upFavourApp(int position) {
        for (int i = 0; i < mApps.size(); i++) {
            mApps.get(i).setIs(false);
            if (i == position)
                mApps.get(i).setIs(true);
        }
        mAppsAdapter.setData(mApps);
        ResolveInfo info = mApps.get(position);

        //该应用的包名
        String pkg = info.activityInfo.packageName;
        //应用的主activity类
        String cls = info.activityInfo.name;
        builder.update(pkg,cls);
        String name = info.activityInfo.loadLabel(this.getPackageManager()).toString();
        SharedPreferencesUtils.setParam(this, "pkg", pkg);
        SharedPreferencesUtils.setParam(this, "cls", cls);
        SharedPreferencesUtils.setParam(this, "name", name);
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
}
