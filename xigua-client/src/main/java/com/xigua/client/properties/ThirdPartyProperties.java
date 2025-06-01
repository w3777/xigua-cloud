package com.xigua.client.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName ThirdPartyProperties
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/1 16:25
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "third-party")
public class ThirdPartyProperties {
    // ip地址查询接口地址
    private String locationApiUrl;
    // ip地址接口请求Key
    private String locationApiKey;
    // 天气查询接口地址
    private String weatherApiUrl;
    // 天气接口请求Key
    private String weatherApiKey;


    @PostConstruct
    public void checkProperties() {
        if (StringUtils.isEmpty(locationApiUrl)) {
            log.error("--------->>>>>>> third-party.locationApiUrl 未配置！");
        }
        if (!StringUtils.isEmpty(locationApiKey)) {
            log.error("--------->>>>>>> third-party.locationApiKey 未配置！");
        }
        if (!StringUtils.isEmpty(weatherApiUrl)) {
            log.error("--------->>>>>>> third-party.weatherApiUrl 未配置！");
        }
        if (!StringUtils.isEmpty(weatherApiKey)) {
            log.error("--------->>>>>>> third-party.weatherApiKey 未配置！");
        }
    }
}
