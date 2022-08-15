package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.config.ApplicationProperties;
import com.tencent.wxcloudrun.dao.DeviceMapper;
import com.tencent.wxcloudrun.dvo.DeviceStatus;
import com.tencent.wxcloudrun.model.Device;
import com.tencent.wxcloudrun.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    public static final String statusUrl = "https://api.huaruanzc.net/backend-api/deviceManagerController.do?getBindedDeviceList";
    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ApplicationProperties applicationProperties;

    @Override
    public List<Device> getList(String city, Integer department) {
        return deviceMapper.findByCityAndDepartment(city, department);
    }

    public DeviceStatus getStatus(String id){
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
        linkedMultiValueMap.add("sn", "");
        linkedMultiValueMap.add("groupid", "");
        linkedMultiValueMap.add("val", "1");
        linkedMultiValueMap.add("type", "onlineflag");
        linkedMultiValueMap.add("devicename", "");
        linkedMultiValueMap.add("page", "1");
        linkedMultiValueMap.add("rows", "15");
        linkedMultiValueMap.add("item_groupid", "");
        linkedMultiValueMap.add("startInput", "");
        linkedMultiValueMap.add("endInput", "");
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.set("Authorization", applicationProperties.getAuthorization());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, httpHeaders);
        String result = restTemplate.postForObject(applicationProperties.getStatusUrl(), httpEntity, String.class);
        System.out.println(result);
        return null;
    }

    public static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Safari/537.36");
        headers.set("accept", "application/json; charset=utf-8");
        headers.set("Sec-Fetch-Dest", "empty");
        headers.set("Sec-Fetch-Mode", "cors");
        headers.set("Sec-Fetch-Site", "same-site");
        headers.set("Sec-GPC", "1");
        headers.set("Origin", "https://backend.huaruanzc.net");
        headers.set("Referer", "https://backend.huaruanzc.net/");
        headers.set("Host", "api.huaruanzc.net");
        return headers;
    }
}
