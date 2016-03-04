package com.intel.jiejia.mycsdkdemo.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single modifiable provider option.
 *
 */
public final class ProviderOption {
    private final String mName;
    private final List<String> mPossibleValues;
    private String mCurrentValue;
    
    /**
     * Construct from an enumeration with the provided default value. The name of the option will
     * be the simple name of the enumeration. The possible values will be all of the possible
     * values of the enumeration. The default value will be the provided instance of the enumeration.
     * @param enumType The type of the enumeration.
     * @param defaultValue The default value for this option.
     * @param <T> The type of the enumeration this Provider Option is created from.
     * @return The constructed provider option.
     */
    public static <T extends Enum<T>> ProviderOption createFromEnum(Class<T> enumType, T defaultValue) {
        List<String> possibleValues = new ArrayList<String>();
        for (T c : enumType.getEnumConstants()) {
            possibleValues.add(c.toString());
        }
        
        return new ProviderOption(enumType.getSimpleName(), possibleValues, defaultValue.toString());
    }
    
    /**
     * Construct a Provider Option from the provided values.
     * @param name The name of the provider option.
     * @param possibleValues All possible values the option can take.
     * @param defaultValue The initial value for the option.
     */
    public ProviderOption(String name, List<String> possibleValues, String defaultValue) {
        mName = name;
        mPossibleValues = Collections.unmodifiableList(possibleValues);
        setCurrentValue(defaultValue);
    }
    
    /**
     * Get the name of the provider option.
     * @return The name of the provider option.
     */
    public String getName() {
        return mName;
    }
    
    /**
     * Gets the list of all possible values this option can take.
     * @return The list of all possible values this option can take.
     */
    public List<String> getPossibleValues() {
        return mPossibleValues;
    }
    
    /**
     * Get the current value of this provider option.
     * @return The current value of the option.
     */
    public String getCurrentValue() {
        return mCurrentValue;
    }
    
    /**
     * Sets a new current value for this option. The value set must be contained in the list
     * of possible values.
     * @param newCurrentValue The new value to set. 
     * @throws IllegalArgumentException If the provided current value is not found in the range of possible values.
     */
    public void setCurrentValue(String newCurrentValue) {
        if (!mPossibleValues.contains(newCurrentValue)) {
            throw new IllegalArgumentException("New current value must be in the range of possible values.");
        }
        
        mCurrentValue = newCurrentValue;
    }
    
    /**
     * Determine if this provider option is convertible to the provided enumeration type.
     * @param enumType The enumeration type to check.
     * @param <T> The type of the enumeration to check.
     * @return True if it is convertible, false otherwise.
     */
    public <T extends Enum<T>> boolean isEnumType(Class<T> enumType) {
        return enumType.getSimpleName().equals(mName);
    }
    
    /**
     * Converts this provider option to an instance of the given enumeration type.
     * @param enumType The target enumeration type.
     * @param <T> The type of the target enumeration.
     * @return The created instance of the enumeration.
     */
    public <T extends Enum<T>> T asEnum(Class<T> enumType) {
        return Enum.valueOf(enumType, mCurrentValue);
    }
}