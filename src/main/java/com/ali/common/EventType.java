package com.ali.common;

import com.ali.utils.CommonUtils;

/**
 * 事件类型
 */
public enum EventType {

REDISTER(CommonUtils.loadApolicationpropertie().getString(CommonData.REGISTER)),
    LOGIN(CommonUtils.loadApolicationpropertie().getString(CommonData.LOGIN)),
    LOGOUT(CommonUtils.loadApolicationpropertie().getString(CommonData.LOGOUT)),
    UPGRADE(CommonUtils.loadApolicationpropertie().getString(CommonData.UPGRADE));


private String eventType;

public String getEventType(){
    return eventType;
}

    EventType(String type) {

    this.eventType=type;
    }
}
