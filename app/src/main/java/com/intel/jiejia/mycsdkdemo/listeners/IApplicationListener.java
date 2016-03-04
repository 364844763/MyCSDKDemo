/**
 * 
 */
package com.intel.jiejia.mycsdkdemo.listeners;

import android.os.Bundle;

import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.jiejia.mycsdkdemo.utils.EventFilter;
import com.intel.jiejia.mycsdkdemo.utils.ProviderOption;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public abstract class IApplicationListener implements ContextTypeListener {
    private final String mName;
    protected final UpdateNotifier mUpdateNotifier;
    private final ContextType mContextType;
    private boolean mIsRunning;
    private Item mLastKnownItem;
    private final List<ProviderOption> mProviderOptions;
    private final EventFilter mEventFilter;

    protected IApplicationListener(String name, ContextType contextType,
                                   UpdateNotifier updateNotifier) {
        mContextType = contextType;
        mUpdateNotifier = updateNotifier;
        mIsRunning = false;
        mName = name;
        mProviderOptions = new ArrayList<ProviderOption>();
        addProviderOptions(mProviderOptions);
        mEventFilter = constructEventFilter();
    }

    public final Item getLastKnownItem() {
        return mLastKnownItem;
    }

    public final void setLastKnownItem(Item item) {
        mLastKnownItem = item;
    }

    public final String getName() {
        return mName + " " + getAdditionalNameInfo();
    }

    public final boolean isRunning() {
        return mIsRunning;
    }

    public final void setIsRunning(boolean isRunning) {
        mIsRunning = isRunning;
        mLastKnownItem = null;
    }

    public final ContextType getContextType() {
        return mContextType;
    }

    public final String describeLastItem() {
        Item lastItem = mLastKnownItem;
        /*if (!mIsRunning || lastItem == null) {
            return "";
        }*/
        if(lastItem==null){
            return "";
        }

        StringBuilder output = new StringBuilder();
        output.append(describeItem(lastItem));
        
        return output.toString();
    }
    
    public abstract boolean shouldNotStartSensing();
    
    protected String getAdditionalNameInfo() {
        return ""; // By default, no additional name information.
    }
    
    public final List<ProviderOption> getProviderOptions() {
        return Collections.unmodifiableList(mProviderOptions);
    }

    protected final <T extends Enum<T>> T getProviderOptionEnum(Class<T> enumType) {
        
        for (ProviderOption option : mProviderOptions) {
            if (option.isEnumType(enumType)) {
                return option.asEnum(enumType);
            }
        }
        
        throw new IllegalArgumentException("Enum of the specified type not found.");
    }

    protected abstract void addProviderOptions(List<ProviderOption> providerOptions);

    /**
     * By default, no settings are specified. Override this to provide specific
     * provider settings to the SDK.
     * 
     * @return The settings for the provider.
     */
    public Bundle getProviderOptionsBundle() {
        return Bundle.EMPTY;
    }
    
    protected abstract EventFilter constructEventFilter();
    
    public final EventFilter getEventFilter() {
        return mEventFilter;
    }

    /**
     * Describe the item in human readable format.
     * 
     * @param item
     *            Item to describe, guaranteed to not be null.
     * @return The description for the last item.
     */
    protected abstract String describeItem(Item item);
    
    protected final void notifyUpdate() {
        mUpdateNotifier.notifyUpdate(describeLastItem());
    }

    public static interface UpdateNotifier {
        void notifyUpdate(String info);
      //  void notifyType(GlyphType type);
        void notifyError(String error);
    }
}
