package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dvo.DeviceStatus;
import com.tencent.wxcloudrun.model.Device;

import java.util.List;

public interface DeviceService {

    List<Device> getList(String city, Integer department);

    DeviceStatus getStatus(String id);
}
