package com.tencent.wxcloudrun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.config")
@Data
public class ApplicationProperties {
    public String authorization;

    public String statusUrl;
}
