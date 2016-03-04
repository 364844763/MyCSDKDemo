package com.intel.jiejia.mycsdkdemo.utils;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A number of providers support filters, which place
 * limits on the types of events reported. This class allows
 * the filters to be displayed and changed for a provider.
 */
public class EventFilter {
    private final Set<String> mPossibleEvents;
    private final Set<String> mCurrentFilter;
    
    public static <T extends Enum<T>> EventFilter createFromEnum(Class<T> enumType, T... defaultFilterElements) {
        HashSet<String> possibleValues = new HashSet<String>();
        for (T current : enumType.getEnumConstants()) {
            possibleValues.add(current.name());
        }
        
        HashSet<String> defaultFilter = new HashSet<String>();
        for (T current : defaultFilterElements) {
            defaultFilter.add(current.name());
        }
        
        return new EventFilter(possibleValues, defaultFilter);
    }
    
    public EventFilter(Set<String> possibleEvents, Set<String> defaultFilter) {
        mPossibleEvents = (possibleEvents != null) ? possibleEvents : Collections.<String>emptySet();
        mCurrentFilter = (defaultFilter != null) ? defaultFilter : new HashSet<String>();
    }
    
    public Set<String> getPossibleEvents() {
        return Collections.unmodifiableSet(mPossibleEvents);
    }
    
    public Set<String> getCurrentFilter() {
        return Collections.unmodifiableSet(mCurrentFilter);
    }
    
    public void addFilterElement(String filterElement) {
        if (!mPossibleEvents.contains(filterElement)) {
            throw new IllegalArgumentException("Invalid filter element.");
        }
        
        mCurrentFilter.add(filterElement);
    }
    
    public void removeFilterElement(String filterElement) {
        mCurrentFilter.remove(filterElement);
    }
    
    public boolean elementIsInFilter(String filterElemenent) {
        return mCurrentFilter.contains(filterElemenent);
    }
    
    public <T extends Enum<T>> T[] getAsEnumArray(Class<T> enumClass) {
        @SuppressWarnings("unchecked")
        T[] retVal = (T[]) Array.newInstance(enumClass, mCurrentFilter.size());
        int index = 0;
        for (String current : mCurrentFilter) {
            retVal[index++] = Enum.valueOf(enumClass, current);
        }
        
        return retVal;
    }
}
