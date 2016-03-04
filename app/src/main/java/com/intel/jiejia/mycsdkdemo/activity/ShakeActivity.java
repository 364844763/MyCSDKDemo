package com.intel.jiejia.mycsdkdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextProviderException;
import com.intel.context.sensing.InitCallback;
import com.intel.jiejia.mycsdkdemo.R;
import com.intel.jiejia.mycsdkdemo.listeners.IApplicationListener;
import com.intel.jiejia.mycsdkdemo.listeners.MySensingListener;
import com.intel.jiejia.mycsdkdemo.listeners.ShakingListener;
import com.intel.jiejia.mycsdkdemo.listeners.ShakingUpdateNotifier;

/**
 * Created by jiejia on 1/20/2016.
 */
public class ShakeActivity extends Activity {
    private ShakingListener mShakingListener;
    private Sensing mSensing;
    public static ShakingUpdateNotifier mUpdateNotifier;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        init();
        setListeners();
    }

    private void setListeners() {



    }

    private void init() {
        mUpdateNotifier=ShakingUpdateNotifier.getIntance();
        mShakingListener=new ShakingListener(mUpdateNotifier);
        mSensing = new Sensing(this, new MySensingListener());

        startDaemon();
        try {
            mSensing.addContextTypeListener(mShakingListener.getContextType(),mShakingListener);
        } catch (ContextProviderException e) {
            e.printStackTrace();
        }

    }

    private void startDaemon() {
        mSensing.start(new InitCallback() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Context Sensing Daemon Started", Toast.LENGTH_SHORT)
                        .show();
                invalidateOptionsMenu();
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
                invalidateOptionsMenu();
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

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mUpdateNotifier.setIs(false);
                mUpdateNotifier.setmContext(ShakeActivity.this);
            }
        }, 1500);
    }
    @Override
    protected void onStop() {
        super.onStop();
//        disableProvider(mShakingListener, mSensing);
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
