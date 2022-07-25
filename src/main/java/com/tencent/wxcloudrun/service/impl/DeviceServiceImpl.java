package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.DeviceMapper;
import com.tencent.wxcloudrun.model.Device;
import com.tencent.wxcloudrun.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DeviceServiceImpl implements DeviceService {
    @Autowired
    DeviceMapper deviceMapper;

    @Override
    public List<Device> getList(String city, Integer department) {
        return deviceMapper.findByCityAndDepartment(city, department);
    }
}
