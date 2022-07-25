package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.DevicesStatusRequest;
import com.tencent.wxcloudrun.model.Device;
import com.tencent.wxcloudrun.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @PostMapping(value = "/api/device")
    public ApiResponse getList(@RequestBody DevicesStatusRequest devicesStatusRequest){
        if(ObjectUtils.isEmpty(devicesStatusRequest.getCity())){
            ApiResponse.error("缺少城市信息");
        }
        List<Device> deviceList = deviceService.getList(devicesStatusRequest.getCity(), devicesStatusRequest.getDepartment());
        return ApiResponse.ok(deviceList);
    }
}
