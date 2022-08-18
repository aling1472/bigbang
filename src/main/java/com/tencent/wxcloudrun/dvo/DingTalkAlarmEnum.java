package com.tencent.wxcloudrun.dvo;

public enum DingTalkAlarmEnum {
    TOKEN_EXPIRE("Token已过期"),
    DEVICE_RESERVE("设备已预定"),
    DEVICE_STATUS_CHANGE_STANDBY("设备状态变化为空闲");

    private final String message;
    DingTalkAlarmEnum(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
