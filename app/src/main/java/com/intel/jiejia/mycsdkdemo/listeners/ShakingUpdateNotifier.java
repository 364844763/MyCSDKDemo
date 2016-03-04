package com.intel.jiejia.mycsdkdemo.listeners;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextProviderException;
import com.intel.context.sensing.InitCallback;
import com.intel.jiejia.mycsdkdemo.activity.MainActivity;
import com.intel.jiejia.mycsdkdemo.activity.ShakeActivity;


/**
 * Created by jiejia on 1/8/2016.
 */
public class ShakingUpdateNotifier implements IApplicationListener.UpdateNotifier {
    private static ShakingUpdateNotifier shakingUpdateNotifier;
    private boolean is=true;
    private Context mContext;

    private ShakingUpdateNotifier(){

    }
    public static ShakingUpdateNotifier getIntance(){


        if (shakingUpdateNotifier==null){
            shakingUpdateNotifier=new ShakingUpdateNotifier();

        }
        return shakingUpdateNotifier;
    }

    public boolean is() {
        return is;
    }

    public void setIs(boolean is) {
        this.is = is;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void notifyUpdate(String info) {
        new android.os.Handler().post(new Runnable() {
            @Override
            public void run() {
//                ScreenShot.takeScreenShot();
//                ScreenShot.shoot((Activity) mContext);
                if (is){
                    Intent intent=new Intent(mContext,ShakeActivity.class);
                    mContext.startActivity(intent);
                }else {
                    Intent intent=new Intent(mContext,MainActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void notifyError(String error) {

    }
    private void startDaemon(final Sensing mSensing, final IApplicationListener mListening) {
        mSensing.start(new InitCallback() {
            public void onSuccess() {
                Toast.makeText(mContext,
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
                Toast.makeText(mContext,
                        "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();

            }
        });
    }
    private void enableProvider(Sensing mSensing,IApplicationListener listeners) {
        try {
            mSensing.addContextTypeListener(listeners.getContextType(), listeners);
            mSensing.enableSensing(listeners.getContextType(), listeners.getProviderOptionsBundle());
        } catch (ContextProviderException e) {
            Toast.makeText(mContext, "Error adding listener to provider: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("jj", "Error adding listener: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Toast.makeText(mContext, "Error adding listener to provider: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("jj", "Error adding listener: " + e.getMessage());
        }
    }
}
