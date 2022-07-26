package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tencent.wxcloudrun.config.ApplicationProperties;
import com.tencent.wxcloudrun.dao.DeviceMapper;
import com.tencent.wxcloudrun.dvo.*;
import com.tencent.wxcloudrun.enums.DeviceStatusEnum;
import com.tencent.wxcloudrun.model.Device;
import com.tencent.wxcloudrun.service.DeviceService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>  implements DeviceService {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ApplicationProperties applicationProperties;

    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public List<Device> getList(String city, Integer department) {
        return null;
    }

    @Override
    public Device getStatus(int id) {
        syncStatus();
        return this.getById(id);
    }

    private String dingTalkSign(String timestamp) {
        try {
            String stringToSign = timestamp + "\n" + applicationProperties.getAppSecret();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(applicationProperties.getAppSecret().getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            LOG.error("计算签名中发送错误: {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dingTalkCommand(String timestamp, String sign, ObjectNode body) {
        try {
            String signTarget = dingTalkSign(timestamp);
            if (Objects.isNull(sign) || !sign.equals(signTarget)) {
                throw new RuntimeException("钉钉推送消息，权限校验错误");
            }
            LOG.debug("接收到钉钉推送的消息: {}", body);

            String msg = body.get("text").get("content").asText();
            String[] commands = msg.split(":");
            if (ObjectUtils.isEmpty(commands[0]) ||
                    ObjectUtils.isEmpty(commands[1])) {
                LOG.error("钉钉推送的消息格式不正确: {}", msg);
                throw new RuntimeException("钉钉推送的消息格式不正确");
            }
            switch (commands[0]) {
                case "TOKEN":
                    applicationProperties.setAuthorization(commands[1].trim());
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private boolean sendDingTalkMessage(DingTalkAlarm dingTalkAlarm) {
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, headers);
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String sign = dingTalkSign(timestamp);
            String params = String.format(DeviceService.DING_TALK_URL_PARAM,
                    applicationProperties.getDingToken(),
                    timestamp, sign);

            DingTalkAlarmResponse result = restTemplate.postForObject(
                    applicationProperties.getDingUrl() + params,
                    httpEntity,
                    DingTalkAlarmResponse.class);
            if (Objects.isNull(result)) {
                LOG.error("钉钉消息发送失败, 无返回");
                return false;
            }
            if (!DING_TALK_CODE_NORMAL.equals(result.getErrcode())) {
                LOG.error("钉钉消息发送失败, code: {}, msg: {}", result.getErrcode(), result.getErrmsg());
                return false;
            }
        } catch (Exception e) {
            LOG.error("钉钉消息发送失败, 网络错误");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Scheduled(cron = "* 0/10 * * * ?")
    public void syncStatus() {
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
        try {
            LOG.info("query: {}", linkedMultiValueMap);
            LOG.info("header: {}", httpHeaders);
            LOG.info("url: {}", applicationProperties.getStatusUrl());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, httpHeaders);
            String result = restTemplate.postForObject(applicationProperties.getStatusUrl(), httpEntity, String.class);
            LOG.info("调用设备API，返回消息：{}", result);
            DeviceApiResponse deviceApiResponse = objectMapper.readValue(result, DeviceApiResponse.class);
            if (!API_CODE_NORMAL.equals(deviceApiResponse.getCode())) {
                // TODO 状态异常 发钉钉消息
                sendDingTalkMessage(DingTalkAlarm.of(DingTalkAlarmEnum.TOKEN_EXPIRE));
            } else {
                List<DeviceStatus> deviceStatusList = deviceApiResponse.getData().getList();
                // TODO 存入设备状态
                List<Device> devices = deviceStatusList.stream().map(d -> {
                    Device device = new Device();
                    device.setId(Integer.valueOf(d.getId()));
                    device.setSn(d.getSn());
                    device.setUpdatedAt(LocalDateTime.now());
                    device.setStatus(parseStatus(d.getWtList()).status());
                    return device;
                }).collect(Collectors.toList());
                this.saveBatch(devices);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public DeviceStatusEnum parseStatus(List<LineStatus> mtList) {
        for (int i = 0; i < mtList.size(); i++) {
            if (DeviceService.API_LINE_STATUS_STANDBY.equals(mtList.get(i).getWkstoptime())) {
                return DeviceStatusEnum.WORKING;
            }
        }
        return DeviceStatusEnum.STANDBY;
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
