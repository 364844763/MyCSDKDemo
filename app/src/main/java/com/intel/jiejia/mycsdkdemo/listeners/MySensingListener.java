package com.intel.jiejia.mycsdkdemo.listeners;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.sensing.SensingEvent;
import com.intel.context.sensing.SensingStatusListener;

/**
 * Created by jiejia on 1/7/2016.
 */
public class MySensingListener implements SensingStatusListener {

    private final String TAG = MySensingListener.class.getName();

    public MySensingListener() {
    }

    public void onEvent(SensingEvent event) {
        Log.i(TAG, "Event: " + event.getDescription());
    }

    public void onFail(ContextError error) {
        Log.e(TAG, "Context Sensing error: " + error.getMessage());
    }
}
