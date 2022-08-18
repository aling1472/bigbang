package com.tencent.wxcloudrun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tencent.wxcloudrun.model.Device;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.List;

public interface DeviceService extends IService<Device> {

    String DING_TALK_URL_PARAM = "access_token=%s&timestamp=%s&sign=%s";

    String API_CODE_NORMAL = "0";

    String DING_TALK_CODE_NORMAL = "0";

    String API_LINE_STATUS_STANDBY = "未工作";



    List<Device> getList(String city, Integer department);

    Device getStatus(int id);

    void dingTalkCommand(String timestamp, String sign, JSONObject body);
}
