package com.xigua.ai.config;

import com.xigua.ai.client.DeepSeekClient;
import com.xigua.ai.llm.properties.DeepseekLLMProperties;
import com.xigua.common.sequence.properties.SequenceSnowflakeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName LLMConfig
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 21:37
 */
@Slf4j
@Configuration
public class LLMConfig {
    @Bean
    @ConditionalOnBean(DeepseekLLMProperties.class)
    @ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "deepseek")
    public DeepSeekClient deepSeekClient(DeepseekLLMProperties properties){
        log.info("DeepSeekClient init, model: {}", properties.getModel());
        DeepSeekClient client = DeepSeekClient.builder()
                .apiKey(properties.getApiKey())
                .model(properties.getModel())
                .build();
        log.info("DeepSeekClient success");
        return client;
    }
}
