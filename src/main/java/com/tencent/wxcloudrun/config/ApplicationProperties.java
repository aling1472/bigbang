package com.tencent.wxcloudrun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application.config")
@Data
@Component
public class ApplicationProperties {
    public String authorization;

    public String statusUrl;
}
