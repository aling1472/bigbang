package com.tencent.wxcloudrun.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.DevicesStatusRequest;
import com.tencent.wxcloudrun.dvo.DeviceApiResponse;
import com.tencent.wxcloudrun.model.Device;
import com.tencent.wxcloudrun.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value="/api/test/{id}")
    public ApiResponse test(@PathVariable int id){
        return ApiResponse.ok(deviceService.getStatus(id));
    }

    @PostMapping(value="/api/dingtalk/msg")
    public ApiResponse dingTalkMsgPush(@RequestHeader(value="timestamp", required=true) String timestamp,
                                       @RequestHeader(value="sign",  required=true) String sign,
                                       @RequestBody JSONObject body){
        deviceService.dingTalkCommand(timestamp,  sign, body);
        return ApiResponse.ok();
    }

    @PostMapping(value = "/api/auth")
    public ApiResponse updateAuth(@RequestBody String auth){
        return ApiResponse.ok();
    }

    public static void main(String[] args) throws JsonProcessingException {
        String line = "{\"code\":0,\"msg\":\"成功\",\"data\":{\"total\":1,\"twoMonthAfterTime\":\"2022/10/15 23:59:59\",\"page\":\"1\",\"list\":[{\"id\":\"100003504\",\"sn\":\"207405339\",\"bindtime\":\"2021/03/07 20:38:30\",\"starttime\":\"2021/02/26 00:05:00\",\"endtime\":\"2023/01/31 23:59:59\",\"devicename\":\"小小集娱-无人茶馆\",\"type\":\"40\",\"enableflag\":\"1\",\"muxcnt\":\"06\",\"groupid\":\"200002034\",\"connecttype\":\"2\",\"groupname\":\"小小集娱兴安路1店\",\"onlineflag\":\"1\",\"itemgroupname\":\"日间茶馆\",\"sound1\":null,\"sound2\":null,\"sound3\":null,\"wtList\":[{\"wkstoptime\":\"未工作\",\"muxnum\":1},{\"wkstoptime\":\"未工作\",\"muxnum\":2},{\"wkstoptime\":\"未工作\",\"muxnum\":3},{\"wkstoptime\":\"未工作\",\"muxnum\":4},{\"wkstoptime\":\"未工作\",\"muxnum\":5},{\"wkstoptime\":\"未工作\",\"muxnum\":6}]}],\"nowTime\":\"2022/08/15 23:59:59\"}}";
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                ;
        DeviceApiResponse d = objectMapper.readValue(line, DeviceApiResponse.class);
        System.out.println(d.getCode());
    }
}
