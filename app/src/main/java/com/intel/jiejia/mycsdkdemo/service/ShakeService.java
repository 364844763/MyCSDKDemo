package com.intel.jiejia.mycsdkdemo.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextProviderException;
import com.intel.context.sensing.InitCallback;
import com.intel.jiejia.mycsdkdemo.bean.MyResolveInfo;
import com.intel.jiejia.mycsdkdemo.listeners.IApplicationListener;
import com.intel.jiejia.mycsdkdemo.listeners.MySensingListener;
import com.intel.jiejia.mycsdkdemo.listeners.ShakingListener;
import com.intel.jiejia.mycsdkdemo.utils.SharedPreferencesUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by jiejia on 1/27/2016.
 */
public class ShakeService extends Service{
    boolean flag;
    private ShakingListener mShakingListener;
    private Sensing mSensing;
    private IApplicationListener.UpdateNotifier mUpdateNotifier;
    private String packageName;
    private List<MyResolveInfo> mApps;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        flag = true;
        initData();
        loadApps();
        new Thread(){
            @Override
            public void run() {
                while(flag){
                    if (getProcessName().isEmpty()||"".equals(getProcessName())||getProcessName().equals("com.android.launcher")||getProcessName().equals("com.google.android.googlequicksearchbox"))
                        continue;
                    packageName= getProcessName();
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }

            }
        }.start();

        super.onCreate();

    }

    private void initData() {
        mUpdateNotifier = new IApplicationListener.UpdateNotifier() {
            @Override
            public void notifyUpdate(String info) {
                if (getProcessName().isEmpty()||"".equals(getProcessName())||getProcessName().equals("com.android.launcher")||getProcessName().equals("com.google.android.googlequicksearchbox")){
                    for (MyResolveInfo resolveInfo:mApps) {

                        if (resolveInfo.activityInfo.packageName.equals(packageName)){
                            ComponentName componet = new ComponentName(packageName, resolveInfo.activityInfo.name);
                            Intent i = new Intent();
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.setComponent(componet);
                            startActivity(i);
                            return;
                        }

                    }

                }

                PackageManager pm = getPackageManager();
                ResolveInfo homeInfo = pm.resolveActivity(
                        new Intent(Intent.ACTION_MAIN)
                                .addCategory(Intent.CATEGORY_HOME), 0);
                ActivityInfo ai = homeInfo.activityInfo;
                Intent startIntent = new Intent(Intent.ACTION_MAIN);
                startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startIntent
                        .setComponent(new ComponentName(ai.packageName, ai.name));
                startActivity(startIntent);
                return ;

            }

            @Override
            public void notifyError(String error) {

            }
        };
        mShakingListener=new ShakingListener(mUpdateNotifier);
        mSensing = new Sensing(this, new MySensingListener());
        startDaemon();
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

    @Override
    public void onDestroy() {
        disableProvider(mShakingListener,mSensing);
        super.onDestroy();
    }

    private String getProcessName() {
        String foregroundProcess = "";
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        // Process running
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);
            // Sort the stats by the last time used
            if(stats != null) {
                SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                }
                if(mySortedMap != null && !mySortedMap.isEmpty()) {
                    String topPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    foregroundProcess = topPackageName;
                }
            }
        } else {
            @SuppressWarnings("deprecation") ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
            foregroundProcess = foregroundTaskInfo.topActivity.getPackageName();

        }
        return foregroundProcess;
    }
    private void startDaemon() {
        mSensing.start(new InitCallback() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Context Sensing Daemon Started", Toast.LENGTH_SHORT)
                        .show();
                enableProvider(mShakingListener);
                /*
                 * After successfully starting the Context Sensing Daemon, we
                 * can enable the sensing of context states such as activity
                 * recognition, location, etc.
                 */


            }

            public void onError(ContextError error) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();

                enableProvider(mShakingListener);

            }
        });

    }

    private void enableProvider(IApplicationListener listeners) {
        try {
            mSensing.addContextTypeListener(listeners.getContextType(), listeners);
            mSensing.enableSensing(listeners.getContextType(), listeners.getProviderOptionsBundle());
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error adding listener to provider: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("jj", "Error adding listener: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "Error adding listener to provider: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("jj", "Error adding listener: " + e.getMessage());
        }
    }
    private void disableProvider(IApplicationListener listener,Sensing mySensing) {

        try {

            if (!listener.shouldNotStartSensing()) {
                mySensing.disableSensing(listener.getContextType());
            }
            mySensing.removeContextTypeListener(listener);
            listener.setIsRunning(false);
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error removing listener from provider: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }




}
