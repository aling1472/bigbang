package com.tencent.wxcloudrun.dvo;

import lombok.Data;

@Data
public class DeviceApiResponse {
    private String code;
    private String msg;
    private DeviceApiData data;
}
