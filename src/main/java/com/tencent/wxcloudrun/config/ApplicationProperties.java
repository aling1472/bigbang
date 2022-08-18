package com.tencent.wxcloudrun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application.config")
@Data
@Component
public class ApplicationProperties {
    private String authorization;

    private String statusUrl;

    private String dingUrl;

    private String dingToken;

    private String appSecret;
}
