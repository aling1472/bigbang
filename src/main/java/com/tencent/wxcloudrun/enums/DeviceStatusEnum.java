package com.tencent.wxcloudrun.enums;

public enum DeviceStatusEnum {
    STANDBY("standby"),
    WORKING("working"),
    UNKNOWN("unknown");

    private final String status;

    DeviceStatusEnum(String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }
}
