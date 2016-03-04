package com.intel.jiejia.mycsdkdemo.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextProviderException;
import com.intel.context.sensing.InitCallback;
import com.intel.jiejia.mycsdkdemo.R;
import com.intel.jiejia.mycsdkdemo.activity.MainActivity;
import com.intel.jiejia.mycsdkdemo.listeners.IApplicationListener;
import com.intel.jiejia.mycsdkdemo.listeners.MySensingListener;
import com.intel.jiejia.mycsdkdemo.listeners.TappingListener;
import com.intel.jiejia.mycsdkdemo.utils.SharedPreferencesUtils;

/**
 * Created by jiejia on 1/14/2016.
 */
public class MyService extends Service {

    private Sensing mTappingSensing;
    private TappingListener mTappingListener;
    private IApplicationListener.UpdateNotifier mUpdateNotifier;
    private String pkg;
    private String cls;
    private MyBinder binder;
    public class MyBinder extends Binder {
        public void update(String pkg1,String cls1){
            pkg=pkg1;
            cls=cls1;
        }
        public void stopSetvice(){
            onDestroy();
        }

    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        binder=new MyBinder();
        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("csdk");
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(110,builder.build());
        //该应用的包名
        pkg = (String) SharedPreferencesUtils.getParam(MyService.this, "pkg", "");
        //应用的主activity类
        cls = (String) SharedPreferencesUtils.getParam(MyService.this, "cls", "");
        setListeners();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //该应用的包名
        pkg = (String) SharedPreferencesUtils.getParam(MyService.this, "pkg", "");
        //应用的主activity类
        cls = (String) SharedPreferencesUtils.getParam(MyService.this, "cls", "");
        return super.onStartCommand(intent, flags, startId);
    }


    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    private void setListeners() {

        mUpdateNotifier = new IApplicationListener.UpdateNotifier() {
            @Override
            public void notifyUpdate(String info) {
                if ("".equals(pkg) || "".equals(cls))
                    return;
                if (!info.equals("DOUBLE_TAP"))
                    return;
                ComponentName componet = new ComponentName(pkg, cls);
                Intent i = new Intent();
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setComponent(componet);
                startActivity(i);
            }

            @Override
            public void notifyError(String error) {

            }
        };
        mTappingListener = new TappingListener(mUpdateNotifier);

        mTappingSensing = new Sensing(this, new MySensingListener());
        startDaemon(mTappingSensing, mTappingListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean stopService(Intent name) {


        return super.stopService(name);
    }

    @Override
    public void onDestroy() {

        disableProvider(mTappingListener,mTappingSensing);
        super.onDestroy();
    }

    private void startDaemon(final Sensing mSensing, final IApplicationListener mListening) {
        mSensing.start(new InitCallback() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Context Sensing Daemon Started", Toast.LENGTH_SHORT)
                        .show();
//                try {
//                    mSensing.enableSensing(mListening.getContextType(), mListening.getProviderOptionsBundle());
//                } catch (ContextProviderException e) {
//                    e.printStackTrace();
//                }
                enableProvider(mSensing, mListening);
                /*
                 * After successfully starting the Context Sensing Daemon, we
                 * can enable the sensing of context states such as activity
                 * recognition, location, etc.
                 */


            }

            public void onError(ContextError error) {
                enableProvider(mSensing, mListening);
                Toast.makeText(getApplicationContext(),
                        "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void enableProvider(Sensing mSensing, IApplicationListener listeners) {
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
