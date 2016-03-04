package com.intel.jiejia.mycsdkdemo.bean;

/**
 * Created by jiejia on 1/19/2016.
 */
public class DemoInfo {
    private Class clz;
    private String name;
    private int iconId;
    private String descripition;
    private Class service;

    public DemoInfo(Class clz, String name, int iconId, String descripition) {
        this.clz = clz;
        this.name = name;
        this.iconId = iconId;
        this.descripition = descripition;
    }

    public Class getService() {
        return service;
    }

    public void setService(Class service) {
        this.service = service;
    }

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getDescripition() {
        return descripition;
    }

    public void setDescripition(String descripition) {
        this.descripition = descripition;
    }
}
