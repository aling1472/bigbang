package com.tencent.wxcloudrun.enums;

public enum DeviceStatus {
    STANDBY("standby"),
    WORKING("working"),
    UNKNOWN("unknown");

    private final String status;

    DeviceStatus(String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }
}
