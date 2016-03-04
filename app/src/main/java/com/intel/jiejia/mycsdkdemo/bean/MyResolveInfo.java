package com.intel.jiejia.mycsdkdemo.bean;

import android.annotation.TargetApi;
import android.content.pm.ResolveInfo;
import android.os.Build;

/**
 * Created by jiejia on 1/14/2016.
 */
public class MyResolveInfo extends ResolveInfo {
    public MyResolveInfo(boolean is) {
        this.is = is;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public MyResolveInfo(ResolveInfo orig, boolean is) {
        super(orig);
        this.is = is;
    }

    private boolean is;

    public boolean is() {
        return is;
    }

    public void setIs(boolean is) {
        this.is = is;
    }
}
