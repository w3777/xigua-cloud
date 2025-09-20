package com.xigua.ai.llm.properties;

import com.xigua.ai.llm.enums.ApiType;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName LLMConfig
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 13:31
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "llm")
public class LLMProperties {
    private String provider;
    private String apiType;
    private int maxTokens;
    private double temperature;

    @PostConstruct
    public void initDefaults() {
        if (StringUtils.isEmpty(provider)) {
            log.info("LLM provider is empty");
            return;
        }
        if (StringUtils.isEmpty(apiType)) {
            apiType = ApiType.OPEN_AI.getType();
        }
        if (maxTokens == 0) { // int 默认是 0
            maxTokens = 1024;
        }
        if (temperature == 0.0) { // double 默认是 0.0
            temperature = 0.7;
        }
    }
}
